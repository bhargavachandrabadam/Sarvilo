package com.easy.stazy.pgmanagement.admin.repository;

import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PgOwnerRepository extends JpaRepository<PgOwnerEntity, Long> {
    Optional<PgOwnerEntity> findByOwnerContactNumber(String ownerContactNumber);

}

