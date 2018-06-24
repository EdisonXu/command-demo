package com.ex.commanddemo.concurrent.executors;

import com.ex.commanddemo.concurrent.command.Command;
import com.ex.commanddemo.concurrent.dispatcher.CommandDispatcher;
import com.ex.commanddemo.concurrent.events.CommandEvent;
import com.ex.commanddemo.concurrent.events.EventBus;
import com.ex.commanddemo.concurrent.handler.CommandHandlerContext;
import com.ex.commanddemo.concurrent.message.LockablePartition;
import com.ex.commanddemo.config.command.CommandConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by edison
 * On 2018/5/15 16:43
 */
public abstract class AbstractCommandExecutor<T>{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommandExecutor.class);

    protected EventBus eventBus;
    protected EventBusProxy eventBusProxy;
    protected CommandConfig commandConfig;
    protected CommandHandlerContext context;
    protected TransactionStatus status;

    public AbstractCommandExecutor(EventBus eventBus, CommandConfig commandConfig, CommandDispatcher commandDispatcher) {
        this(eventBus, commandConfig, null, commandDispatcher);
    }

    public AbstractCommandExecutor(EventBus eventBus, CommandConfig commandConfig, LockablePartition partition, CommandDispatcher commandDispatcher) {
        this.eventBus = eventBus;
        this.commandConfig = commandConfig;
        eventBusProxy = new EventBusProxy(eventBus);
        context = new CommandHandlerContext(commandConfig, partition, commandDispatcher, commandConfig.getSpringEventPublisher());
    }

    public void startTransaction(){
        PlatformTransactionManager platformTransactionManager = commandConfig.getPlatformTransactionManager();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        status = platformTransactionManager.getTransaction(def);
    }

    public void commit(){
        commandConfig.getPlatformTransactionManager().commit(status);
        eventBusProxy.commit();
    }

    public void rollback(Command command){
        LOGGER.error("Failed to execute command {}!", command);
        if(status!=null)
            commandConfig.getPlatformTransactionManager().rollback(status);
    }

    protected static class EventBusProxy implements EventBus{
        private EventBus eventBus;
        private List<CommandEvent> events;
        private boolean isAsync;

        public EventBusProxy(EventBus eventBus) {
            this.eventBus = eventBus;
            this.events = new ArrayList<>();
        }

        @Override
        public void aSyncPublish(CommandEvent event) {
            isAsync = true;
            Optional.ofNullable(event).ifPresent(events::add);
        }

        @Override
        public void publish(CommandEvent event) {
            Optional.ofNullable(event).ifPresent(events::add);
        }

        public void commit(){
            LOGGER.debug("Commit current events into real queue");
            if(isAsync)
                this.eventBus.aSyncPublish(events);
            else
                this.eventBus.publish(events);
        }
    }

    public abstract T execute();
}
