package com.ex.commanddemo.service;

import com.ex.commanddemo.domain.ReduceMoneyEvent;
import com.ex.commanddemo.repo.MoneyEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author edison
 * On 2018/6/22 18:27
 */
@Service
public class EventService {

	@Autowired
	private MoneyEventRepository repository;

	@Transactional
	@javax.transaction.Transactional
	public void saveEvent(ReduceMoneyEvent event){
		repository.save(event);
	}
}
