package com.ex.commanddemo.concurrent.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ex.commanddemo.concurrent.message.MessageQueue.MAX_PARTITION_NUM;


/**
 * Created by edison
 * On 2018/5/15 20:33
 */
public class CommandHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHelper.class);

    public static int getPartitionId(long num){
        return (int)num%MAX_PARTITION_NUM;
    }
}
