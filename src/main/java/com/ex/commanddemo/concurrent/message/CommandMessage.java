package com.ex.commanddemo.concurrent.message;


import com.ex.commanddemo.concurrent.command.Command;
import com.ex.commanddemo.concurrent.command.ExecutionResult;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by edison
 * On 2018/5/15 18:13
 */
public class CommandMessage implements Serializable {

    private static final long serialVersionUID = 494446819947177653L;

    private Command command;
    private String name;
    private long createdTime;
    private long timeout;
    private long timeoutTime;
    private CountDownLatch countDownLatch;
    private ExecutionResult result;

    public CommandMessage(Command command, long timeout) {
        this.command = command;
        this.name = command.getClass().getSimpleName();
        this.createdTime = System.currentTimeMillis();
        this.timeout = timeout;
        this.timeoutTime = this.createdTime+timeout;
        this.countDownLatch = new CountDownLatch(1);
        this.result = ExecutionResult.successResult();
    }

    public static <T extends Command> CommandMessage wrap(T command, long timeout){
        return new CommandMessage(command, timeout);
    }

    public Command getCommand() {
        return command;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public ExecutionResult getResult() {
        return result;
    }

    public void setResult(ExecutionResult result) {
        this.result = result;
    }

    public void setResultCode(ExecutionResult.Code code, String msg){
        this.result.setResultCode(code);
        this.result.setMsg(msg);
    }

    public long getTimeoutTime() {
        return timeoutTime;
    }

    public String getName() {
        return name;
    }

    public long getTimeout() {
        return timeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandMessage that = (CommandMessage) o;
        return createdTime == that.createdTime &&
            timeout == that.timeout &&
            timeoutTime == that.timeoutTime &&
            Objects.equals(command, that.command) &&
            Objects.equals(name, that.name) &&
            Objects.equals(countDownLatch, that.countDownLatch) &&
            Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {

        return Objects.hash(command, name, createdTime, timeout, timeoutTime, countDownLatch, result);
    }

    @Override
    public String toString() {
        return "CommandMessage{" +
            "command=" + command +
            ", name='" + name + '\'' +
            ", createdTime=" + createdTime +
            ", timeout=" + timeout +
            ", timeoutTime=" + timeoutTime +
            ", countDownLatch=" + countDownLatch +
            ", result=" + result +
            '}';
    }

    public boolean awaitResult(long timeoutInMs, TimeUnit timeUnit) throws InterruptedException {
        return this.countDownLatch.await(timeoutInMs, timeUnit);
    }
}
