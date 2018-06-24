package com.ex.commanddemo.concurrent.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by edison
 * On 2018/5/15 18:05
 */
public class InMemoryCommandMessageQueue implements MessageQueue<CommandMessage> {

    private Map<String, Map<Integer, LockablePartition>> queue = new ConcurrentHashMap<>();

    @Override
    public LockablePartition offer(CommandMessage message, String topicName, int partitionId) {
        Map<Integer, LockablePartition> topic = getOrCreateTopic(topicName);
        LockablePartition lockablePartition = getOrCreatePartition(topicName, partitionId, topic);
        lockablePartition.offer(message);
        return lockablePartition;
    }

    private LockablePartition getOrCreatePartition(String topicName,int partitionId, Map<Integer, LockablePartition> topic) {
        LockablePartition newQueue = new LockablePartition(topicName, partitionId);
        LockablePartition lockablePartition = topic.putIfAbsent(partitionId, newQueue);
        lockablePartition = lockablePartition ==null ? newQueue : lockablePartition;
        return lockablePartition;
    }

    private Map<Integer, LockablePartition> getOrCreateTopic(String topic) {
        Map<Integer, LockablePartition> newPartitions = new ConcurrentHashMap<>();
        Map<Integer, LockablePartition> partitions = queue.putIfAbsent(topic, newPartitions);
        partitions = partitions==null ? newPartitions : partitions;
        return partitions;
    }

    @Override
    public CommandMessage pull(String topicName, int partitionId) {
        Map<Integer, LockablePartition> topic = queue.get(topicName);
        if(topic==null)
            return null;
        LockablePartition partition = topic.get(partitionId);
        if(partition==null || !partition.tryLock())
            return null;
        return partition.pull();
    }

}
