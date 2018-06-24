package com.ex.commanddemo.concurrent.message;

import org.slf4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Edison
 * On 2018/3/25 23:18
 */
public class LockablePartition {


	private static final Logger LOGGER = getLogger(LockablePartition.class);
	private ConcurrentLinkedQueue<CommandMessage> queue;
	private int id;
	private AtomicBoolean isLocked;
    private String topicName;

    public LockablePartition(String topicName, int id) {
        this.topicName = topicName;
        this.id = id;
        this.queue = new ConcurrentLinkedQueue<>();
        this.isLocked = new AtomicBoolean(false);
    }

    public int getId() {
        return id;
    }

    public boolean tryLock(){
        boolean result = isLocked.compareAndSet(false, true);
        LOGGER.debug("Thread {} takes queue of {}-{} result: {}", Thread.currentThread(), topicName, id, result);
        return result;
    }

    public boolean release(){
        return isLocked.compareAndSet(true, false);
    }

    public boolean offer(CommandMessage command){
        LOGGER.debug("Offer new command: {}", command);
        return queue.offer(command);
    }

    public CommandMessage pull() {
        CommandMessage command = queue.poll();
        LOGGER.debug("Thread {} take command {} from queue of group {}", Thread.currentThread(), command, id);
        return command;
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }

    public void clean() {
        if(!isEmpty())
            queue.clear();
    }

    public String getTopicName() {
        return topicName;
    }

    @Override
    public String toString() {
        return "ThreadBoundPartition{" +
            "id=" + id +
            ", isLocked=" + isLocked +
            '}';
    }
}
