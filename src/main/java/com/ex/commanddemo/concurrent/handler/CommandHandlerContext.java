package com.ex.commanddemo.concurrent.handler;

import com.ex.commanddemo.concurrent.dispatcher.CommandDispatcher;
import com.ex.commanddemo.concurrent.message.LockablePartition;
import com.ex.commanddemo.config.command.CommandConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Created by edison
 * On 2018/5/15 22:05
 */
public class CommandHandlerContext {

    private CommandConfig commandConfig;
    private ApplicationContext springContext;
    private LockablePartition partition;
    private CommandDispatcher commandDispatcher;
    private ApplicationEventPublisher springEventPublisher;

    public CommandHandlerContext(CommandConfig commandConfig, LockablePartition partition, CommandDispatcher commandDispatcher) {
        this.commandConfig = commandConfig;
        this.springContext = commandConfig.getContext();
        this.partition = partition;
        this.commandDispatcher = commandDispatcher;
    }

    public CommandHandlerContext(CommandConfig commandConfig, LockablePartition partition, CommandDispatcher commandDispatcher, ApplicationEventPublisher springEventPublisher) {
        this.commandConfig = commandConfig;
        this.springContext = commandConfig.getContext();
        this.partition = partition;
        this.commandDispatcher = commandDispatcher;
        this.springEventPublisher = springEventPublisher;
    }

    public CommandConfig getCommandConfig() {
        return commandConfig;
    }


    public ApplicationContext getSpringContext() {
        return springContext;
    }

    public LockablePartition getPartition() {
        return partition;
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    public ApplicationEventPublisher getSpringEventPublisher() {
        return springEventPublisher;
    }
}
