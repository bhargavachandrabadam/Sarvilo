package com.easy.stazy.pgmanagement.pg.repository;

import com.easy.stazy.pgmanagement.pg.entities.RuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends JpaRepository<RuleEntity, Long> {
}

