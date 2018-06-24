package com.ex.commanddemo.repo;

import com.ex.commanddemo.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author edison
 * On 2018/6/24 15:54
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
