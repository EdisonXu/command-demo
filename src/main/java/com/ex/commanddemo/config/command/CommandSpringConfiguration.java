package com.ex.commanddemo.config.command;

import com.ex.commanddemo.concurrent.dispatcher.CommandDispatcher;
import com.ex.commanddemo.concurrent.events.EventBus;
import com.ex.commanddemo.concurrent.events.SimpleEventBus;
import com.ex.commanddemo.concurrent.message.InMemoryCommandMessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by edison
 * On 2018/4/4 14:52
 */
@Configuration
@AutoConfigureAfter(EventHandlerRegistry.class)
public class CommandSpringConfiguration {

    private ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2+1);

	@Autowired
	private PlatformTransactionManager ptm;

	@Autowired
    private ApplicationContext context;

	@Autowired
    private ApplicationEventPublisher eventPublisher;

	@Bean
	public TransactionTemplate transactionTemplate(){
		TransactionTemplate t=  new TransactionTemplate(ptm);
		return t;
	}

	@Bean
	public CommandConfig commandConfig() throws Exception{
		CommandConfig config = new CommandConfig(ptm, context, new CommandHandlerRegistry(), new InMemoryCommandMessageQueue(), eventPublisher);
		return config;
	}

	@Bean
    public EventBus eventBus(){
	    return new SimpleEventBus(pool);
    }

	@Bean
    public CommandDispatcher commandDispatcher() throws Exception {
	    return new CommandDispatcher(commandConfig(), eventBus(), pool);
    }

}
