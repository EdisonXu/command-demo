package com.ex.commanddemo.concurrent.executors;

import com.ex.commanddemo.concurrent.command.Command;
import com.ex.commanddemo.concurrent.command.ExecutionResult;
import com.ex.commanddemo.concurrent.dispatcher.CommandDispatcher;
import com.ex.commanddemo.concurrent.events.EventBus;
import com.ex.commanddemo.concurrent.handler.CommandHandler;
import com.ex.commanddemo.config.command.CommandConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by edison
 * On 2018/5/15 17:19
 */
public class DefaultCommandExecutor extends AbstractCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCommandExecutor.class);

    private Command command;
    private CommandHandler commandHandler;

    public DefaultCommandExecutor(EventBus eventBus, CommandConfig commandConfig, Command command, CommandHandler commandHandler, CommandDispatcher commandDispatcher) {
        super(eventBus, commandConfig, commandDispatcher);
        this.command = command;
        this.commandHandler = commandHandler;
    }

    public ExecutionResult execute(){
        ExecutionResult result;
        try {
            result = commandHandler.on(command, eventBusProxy, context);
        } catch (Exception e) {
           LOGGER.error("", e);
           result = ExecutionResult.failedResult(e.getMessage());
        }
        return result;
    }
}
