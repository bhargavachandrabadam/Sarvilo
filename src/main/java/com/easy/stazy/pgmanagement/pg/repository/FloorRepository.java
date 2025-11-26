package com.easy.stazy.pgmanagement.pg.repository;

import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.entities.FloorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloorRepository extends JpaRepository<FloorEntity, Long> {
    FloorEntity findByFloorNumberAndPg(String floor, PgManagementEntity pg);

    FloorEntity findByPgIdAndFloorNumber(Long pgId, String floor);

    List<FloorEntity> findByPgId(Long pgId);
}

