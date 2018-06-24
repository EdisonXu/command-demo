package com.ex.commanddemo.concurrent.events;

import com.ex.commanddemo.config.command.EventHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by edison
 * On 2018/5/15 17:56
 */
public class SimpleEventBus implements EventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEventBus.class);

    private EventHandlerRegistry registry;
    private ExecutorService pool;

    public SimpleEventBus() {
        this.pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2+1);
    }

    public SimpleEventBus(ExecutorService pool) {
        this.pool = pool;
    }

    public EventHandlerRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(EventHandlerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void aSyncPublish(CommandEvent event) {
        if(event==null){
            LOGGER.warn("Ignore publish NULL command event");
            return;
        }
        pool.submit(()->publish(event));
    }

    @Override
    public void publish(CommandEvent event) {
        if(event==null){
            LOGGER.warn("Ignore publish NULL command event");
            return;
        }
        Optional.ofNullable(registry.getSubscriber(event)).ifPresent(l->l.forEach(s->ReflectionUtils.invokeMethod(s.getHandleMethod(), s.getHandler(), event)));
    }
}
