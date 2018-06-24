package com.ex.commanddemo.concurrent.executors;

import com.ex.commanddemo.concurrent.command.Command;
import com.ex.commanddemo.concurrent.command.ExecutionResult;
import com.ex.commanddemo.concurrent.dispatcher.CommandDispatcher;
import com.ex.commanddemo.concurrent.events.EventBus;
import com.ex.commanddemo.concurrent.message.CommandMessage;
import com.ex.commanddemo.concurrent.message.LockablePartition;
import com.ex.commanddemo.config.command.CommandConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by edison
 * On 2018/5/15 17:18
 */
public class QueuedCommandExecutor extends AbstractCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueuedCommandExecutor.class);

    private LockablePartition partition;

    public QueuedCommandExecutor(EventBus eventBus, CommandConfig commandConfig, LockablePartition partition, CommandDispatcher commandDispatcher) {
        super(eventBus, commandConfig, partition, commandDispatcher);
        this.partition = partition;
    }

    public Void execute(){
        if(!partition.tryLock())
            return null;
        for(;;){
            long start = System.currentTimeMillis();
            CommandMessage cMsg = partition.pull();
            Command command = null;
            try {
                // check queue is empty
                if(cMsg==null)
                    break;
                // check timeout
                if(System.currentTimeMillis()>cMsg.getTimeoutTime()) {
                    LOGGER.debug("Ignore command {} because of timeout", cMsg.getName());
                    continue;
                }
                command = cMsg.getCommand();
                LOGGER.debug("Begin to handle command {}", command);

                cMsg.setResult(Optional.ofNullable(commandConfig.getCommandHandlerRegistry().find(command))
                    .map(handler-> handler.on(cMsg.getCommand(), eventBusProxy, context))
                    .orElse(ExecutionResult.timeoutResult()));

                if(cMsg.getResult().getResultCode().equals(ExecutionResult.Code.SUCCESS))
                	eventBusProxy.commit();
            }catch (Exception e){
                LOGGER.error("", e);
                if(cMsg!=null)
                    cMsg.setResultCode(ExecutionResult.Code.FAILED, e.getMessage());
            } finally{
                /*if(command instanceof DbCommand){
                    context.getSpringEventPublisher().publishEvent(new CommandTransactionEvent(cMsg));
                }else{
                    try {
                        if(cMsg!=null && cMsg.getCountDownLatch()!=null)
                            cMsg.getCountDownLatch().countDown();
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }
                }*/
                long end = System.currentTimeMillis();
                LOGGER.info("Execute handler of {} costs {}ms", cMsg, (end-start));
                try {
                    if(cMsg!=null && cMsg.getCountDownLatch()!=null)
                        cMsg.getCountDownLatch().countDown();
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
        if(partition.release())
            LOGGER.info("Thread {} release queue {}-{}", Thread.currentThread().getId(), partition.getTopicName(), partition.getId());
        return null;
    }

}
