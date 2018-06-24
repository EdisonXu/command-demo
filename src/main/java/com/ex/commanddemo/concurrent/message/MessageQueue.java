package com.ex.commanddemo.concurrent.message;

/**
 * Created by edison
 * On 2018/5/15 17:29
 */
public interface MessageQueue<T> {

    int MAX_PARTITION_NUM = 500;

    LockablePartition offer(T message, String topic, int partition);

    T pull(String topic, int partitionId);

}
