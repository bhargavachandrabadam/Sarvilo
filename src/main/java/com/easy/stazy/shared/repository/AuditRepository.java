package com.easy.stazy.shared.repository;

import com.easy.stazy.shared.entities.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditEntity, Long> {
}
