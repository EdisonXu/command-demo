package com.ex.commanddemo.concurrent.events;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by edison
 * On 2018/5/15 15:02
 */
public interface EventBus {
    void aSyncPublish(CommandEvent event);
    default void aSyncPublish(CommandEvent... events){
        this.publish(Arrays.asList(events));
    }
    default void aSyncPublish(List<? extends CommandEvent> events){
        Optional.ofNullable(events).ifPresent(l->l.forEach(e->aSyncPublish(e)));
    }

    void publish(CommandEvent event);
    default void publish(CommandEvent... events){
        this.publish(Arrays.asList(events));
    }
    default void publish(List<? extends CommandEvent> events){
        Optional.ofNullable(events).ifPresent(l->l.forEach(e->publish(e)));
    }
}
