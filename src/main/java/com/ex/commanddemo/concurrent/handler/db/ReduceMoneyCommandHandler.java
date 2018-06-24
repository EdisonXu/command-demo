package com.ex.commanddemo.concurrent.handler.db;

import com.ex.commanddemo.concurrent.command.ExecutionResult;
import com.ex.commanddemo.concurrent.command.ReduceMoneyCommand;
import com.ex.commanddemo.concurrent.events.EventBus;
import com.ex.commanddemo.concurrent.handler.CommandHandler;
import com.ex.commanddemo.concurrent.handler.CommandHandlerContext;
import com.ex.commanddemo.domain.ReduceMoneyEvent;
import com.ex.commanddemo.repo.MoneyEventRepository;
import com.ex.commanddemo.service.EventService;
import com.ex.commanddemo.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;

/**
 * @author edison
 * On 2018/6/22 17:20
 */
public class ReduceMoneyCommandHandler implements CommandHandler<ReduceMoneyCommand> {


	private static final Logger LOGGER = LoggerFactory.getLogger(ReduceMoneyCommandHandler.class);

	private WalletService walletService;
	private MoneyEventRepository eventRepository;
	private EventService eventService;

	@Override
	@Transactional
	@org.springframework.transaction.annotation.Transactional
	public ExecutionResult on(ReduceMoneyCommand command, EventBus eventBus, CommandHandlerContext context) {
		if(walletService==null)
			walletService = context.getSpringContext().getBean(WalletService.class);
		if(eventRepository==null)
			eventRepository = context.getSpringContext().getBean(MoneyEventRepository.class);
		if(eventService==null)
			eventService = context.getSpringContext().getBean(EventService.class);

		try{
			ReduceMoneyEvent event = command.getEvent();
			walletService.reduce(event.getWalletId(), event.getValue());
			//eventRepository.save(event);
			eventService.saveEvent(event);
		}catch (Exception e){
			LOGGER.error("", e);
			return ExecutionResult.failedResult(e.getMessage());
		}
		return ExecutionResult.successResult();
	}
}
