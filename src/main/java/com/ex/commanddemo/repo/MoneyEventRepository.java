package com.ex.commanddemo.repo;

import com.ex.commanddemo.domain.ReduceMoneyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author edison
 * On 2018/6/22 17:30
 */
@Repository
public interface MoneyEventRepository extends JpaRepository<ReduceMoneyEvent, Long> {
}
