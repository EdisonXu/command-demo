package com.ex.commanddemo.controller;

import com.ex.commanddemo.concurrent.command.BatchDbCommand;
import com.ex.commanddemo.concurrent.command.ExecutionResult;
import com.ex.commanddemo.concurrent.command.ReduceMoneyCommand;
import com.ex.commanddemo.concurrent.dispatcher.CommandDispatcher;
import com.ex.commanddemo.concurrent.exception.CommandExecuteFailedException;
import com.ex.commanddemo.concurrent.exception.CommandExecuteTimeoutException;
import com.ex.commanddemo.domain.AuditLog;
import com.ex.commanddemo.domain.ReduceMoneyEvent;
import com.ex.commanddemo.domain.Wallet;
import com.ex.commanddemo.repo.AuditLogRepository;
import com.ex.commanddemo.repo.WalletRepository;
import com.ex.commanddemo.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author edison
 * On 2018/6/22 17:01
 */
@RestController
@RequestMapping("/wallet")
public class WalletController {

	@Autowired
	private WalletRepository repository;

	@Autowired
	private CommandDispatcher commandDispatcher;

	@Autowired
	private AuditLogRepository auditLogRepository;

	@PostMapping
	public Wallet create(@RequestBody Wallet wallet){
		return repository.save(wallet);
	}

	@PostMapping("/use")
	@Transactional
	public ExecutionResult use(@RequestBody ReduceMoneyEvent reduceMoneyEvent){
		// add the auit log to verify the transaction rollback
		auditLogRepository.save(new AuditLog());
		ExecutionResult result = commandDispatcher.dispatchToQueueAndGet(new ReduceMoneyCommand(reduceMoneyEvent), "wallet", 1);
		switch (result.getResultCode()){
			case SUCCESS:
				break;
			case FAILED:
				throw new CommandExecuteFailedException(result.getMsg());
			default:
				throw new CommandExecuteTimeoutException("Timeout");
		}
		return result;
	}

	@PostMapping("/useb")
	public ExecutionResult useb(@RequestBody ReduceMoneyEvent reduceMoneyEvent){
		BatchDbCommand batchDbCommand = new BatchDbCommand(new ReduceMoneyCommand(reduceMoneyEvent));
		return commandDispatcher.dispatchToQueueAndGet(batchDbCommand, "wallet", 2);
	}

	@GetMapping
	public List<Wallet> list (){
		return repository.findAll();
	}

	@GetMapping("/all")
	public Wallet find(@RequestParam String name){
		return repository.findByName(name);
	}


}
