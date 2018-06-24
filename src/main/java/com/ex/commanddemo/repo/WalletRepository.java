package com.ex.commanddemo.repo;

import com.ex.commanddemo.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author edison
 * On 2018/6/22 17:03
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>, JpaSpecificationExecutor {
	Wallet findByName(String name);
}
