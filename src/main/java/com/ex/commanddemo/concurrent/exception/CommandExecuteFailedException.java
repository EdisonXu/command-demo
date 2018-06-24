package com.ex.commanddemo.concurrent.exception;

/**
 * Created by edison
 * On 2018/4/18 16:11
 */
public class CommandExecuteFailedException extends RuntimeException {
    public CommandExecuteFailedException(String message) {
        super(message);
    }
}
