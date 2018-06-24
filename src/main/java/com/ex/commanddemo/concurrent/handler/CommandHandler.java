package com.ex.commanddemo.concurrent.handler;


import com.ex.commanddemo.concurrent.command.Command;
import com.ex.commanddemo.concurrent.command.ExecutionResult;
import com.ex.commanddemo.concurrent.events.EventBus;

/**
 * Created by Edison
 * On 2018/3/25 22:38
 */
public interface CommandHandler<T extends Command> {

	ExecutionResult on(T command, EventBus eventBus, CommandHandlerContext context);
}
