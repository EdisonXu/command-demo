package com.ex.commanddemo.service;

import com.ex.commanddemo.domain.Wallet;
import com.ex.commanddemo.repo.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;

/**
 * @author edison
 * On 2018/6/22 17:25
 */
@Service
public class WalletService {

	@Autowired
	private WalletRepository repository;

	@Transactional
	public void reduce(long id, long value){
		Wallet wallet = repository.findOne(id);
		if(wallet.getValue()<value)
			throw new ValidationException("余额不足");
		wallet.setValue(wallet.getValue()-value);
		repository.save(wallet);
	}
}
