package com.easy.stazy.pgmanagement.admin.repository;

import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PgManagementRepository extends JpaRepository<PgManagementEntity, Long>, JpaSpecificationExecutor<PgManagementEntity> {

    // Find all PGs belonging to an owner
    List<PgManagementEntity> findByOwner_Id(Long ownerId);

    // Find PG by name, location, and owner
    boolean existsByPgNameAndLocationAndOwner_Id(String pgName, String location, Long ownerId);
}
