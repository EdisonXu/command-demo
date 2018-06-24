package com.ex.commanddemo.concurrent.dispatcher;

import com.ex.commanddemo.concurrent.command.Command;
import com.ex.commanddemo.concurrent.command.ExecutionResult;
import com.ex.commanddemo.concurrent.events.EventBus;
import com.ex.commanddemo.concurrent.executors.DefaultCommandExecutor;
import com.ex.commanddemo.concurrent.executors.QueuedCommandExecutor;
import com.ex.commanddemo.concurrent.message.CommandMessage;
import com.ex.commanddemo.concurrent.message.LockablePartition;
import com.ex.commanddemo.concurrent.message.MessageQueue;
import com.ex.commanddemo.config.command.CommandConfig;
import com.ex.commanddemo.config.command.CommandHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.concurrent.*;

/**
 * Created by Edison
 * On 2018/3/25 22:29
 */
public class CommandDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDispatcher.class);

    private static final long DEFAULT_TIMEOUT_IN_MS = 3000;
    private static final String HANDLER_NOT_FOUND = "Unable to find command handler for command";

    private CommandConfig commandConfig;
    private EventBus eventBus;
    private CommandHandlerRegistry handlerRegistry;
    private MessageQueue messageQueue;
    private ExecutorService workers;

    public CommandDispatcher(CommandConfig commandConfig, EventBus eventBus) {
        this.commandConfig = commandConfig;
        this.handlerRegistry = commandConfig.getCommandHandlerRegistry();
        this.messageQueue = commandConfig.getMessageQueue();
        this.eventBus = eventBus;
        workers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);

    }

    public CommandDispatcher(CommandConfig commandConfig, EventBus eventBus, ExecutorService workers) {
        this.commandConfig = commandConfig;
        this.handlerRegistry = commandConfig.getCommandHandlerRegistry();
        this.messageQueue = commandConfig.getMessageQueue();
        this.eventBus = eventBus;
        this.workers = workers;
    }

    public <T extends Command> Future<ExecutionResult> dispatch(T command){
        return workers.submit(()-> runDefault(command));
    }

    public <T extends Command> ExecutionResult dispatchAndGetResult(T command){
        return dispatchAndGetResult(command, DEFAULT_TIMEOUT_IN_MS);
    }

    public <T extends Command> ExecutionResult dispatchAndGetResult(T command, long timeoutInMs){
        return doDisptachAndGetResult(command, timeoutInMs);
    }

    public <T extends Command> ExecutionResult dispatchInCurrThread(T command){
        Assert.notNull(command, "Cannot dispatch NULL command!");
        return Optional.ofNullable(handlerRegistry.find(command))
            .map(handler-> runDefault(command))
            .orElse(ExecutionResult.failedResult(HANDLER_NOT_FOUND +command.getClass().getSimpleName()));
    }

    public <T extends Command> Future<?> dispatchToQueue(T command, String topicName, int partitionId){
        return dispatchToQueue(command, topicName, partitionId, DEFAULT_TIMEOUT_IN_MS);
    }

    public <T extends Command> Future<Void> dispatchToQueue(T command, String topicName, int partitionId, long timeoutInMs){
        Assert.notNull(command, "Cannot dispatch NULL command!");
        CommandMessage commandMessage = CommandMessage.wrap(command, timeoutInMs);
        LockablePartition partition = this.messageQueue.offer(commandMessage, topicName, partitionId);
        return dispatchToQueue(commandMessage, partition);
    }

    public <T extends Command> Future<Void> dispatchToQueue(CommandMessage commandMessage, LockablePartition partition){
        return Optional.ofNullable(handlerRegistry.find(commandMessage.getCommand()))
            .map(handler-> workers.submit(()-> runQueuedExecutor(partition)))
            .orElse(null);
    }

    public <T extends Command> ExecutionResult dispatchToQueueAndGet(T command, String topicName, int partitionId){
        return dispatchToQueueAndGet(command, topicName, partitionId, DEFAULT_TIMEOUT_IN_MS);
    }

    public <T extends Command> ExecutionResult dispatchToQueueAndGet(T command, String topicName, int partitionId, long timeoutInMs){
        Assert.notNull(command, "Cannot dispatch NULL command!");
        CommandMessage commandMessage = CommandMessage.wrap(command, timeoutInMs);
        LockablePartition partition = this.messageQueue.offer(commandMessage, topicName, partitionId);
        return Optional.ofNullable(dispatchToQueue(commandMessage, partition))
            .map(future-> {
                ExecutionResult result = ExecutionResult.failedResult();
                try {
                    if(commandMessage.awaitResult(commandMessage.getTimeout(), TimeUnit.MILLISECONDS))
                        result = commandMessage.getResult();
                    else {
                        result.setResultCode(ExecutionResult.Code.TIMEOUT);
                        result.setMsg("请求已超时，请重试");
                    }
                } catch (InterruptedException e) {
                    result.setResultCode(ExecutionResult.Code.TIMEOUT);
                    result.setMsg("请求已超时，请重试");
                    LOGGER.error("Command {} is timeout", command, e);
                }
                return result;
            })
            .orElse(ExecutionResult.failedResult(HANDLER_NOT_FOUND +command.getClass().getSimpleName()));
    }

    private <T extends Command> Future<ExecutionResult> doDisptach(T command, Future futureFunc){
        Assert.notNull(command, "Cannot dispatch NULL command!");
        return Optional.ofNullable(handlerRegistry.find(command))
            .map(handler-> futureFunc)
            .orElse(null);
    }

    private <T extends Command> ExecutionResult doDisptachAndGetResult(T command, long timeoutInMs){
        Assert.notNull(command, "Cannot dispatch NULL command!");
        return Optional.ofNullable(dispatch(command))
            .map(future-> {
                try {
                    return future.get(timeoutInMs, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | TimeoutException e) {
                    return ExecutionResult.timeoutResult();
                } catch (ExecutionException e) {
                    return ExecutionResult.failedResult(e.getMessage());
                }
            })
            .orElse(ExecutionResult.failedResult(HANDLER_NOT_FOUND +command.getClass().getSimpleName()));
    }

    private <T extends Command> ExecutionResult runDefault(T command) {
        return new DefaultCommandExecutor(eventBus, commandConfig, command, handlerRegistry.find(command), this).execute();
    }


    private Void runQueuedExecutor(LockablePartition partition) {
        return new QueuedCommandExecutor(eventBus, commandConfig, partition, this).execute();
    }

}
