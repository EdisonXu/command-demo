package com.ex.commanddemo.concurrent.exception;

/**
 * Created by edison
 * On 2018/4/18 16:11
 */
public class CommandExecuteTimeoutException extends RuntimeException {
    public CommandExecuteTimeoutException(String message) {
        super(message);
    }
}
