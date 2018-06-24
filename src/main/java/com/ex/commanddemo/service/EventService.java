package com.ex.commanddemo.service;

import com.ex.commanddemo.domain.ReduceMoneyEvent;
import com.ex.commanddemo.repo.MoneyEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.Random;

/**
 * @author edison
 * On 2018/6/22 18:27
 */
@Service
public class EventService {

	@Autowired
	private MoneyEventRepository repository;
	private boolean switcher;

	@Transactional
	public void saveEvent(ReduceMoneyEvent event){
		if(!switcher)
			switcher = true;
		else{
			switcher = false;
			throw new ValidationException("No enough inventory!");
		}
		repository.save(event);
	}
}
