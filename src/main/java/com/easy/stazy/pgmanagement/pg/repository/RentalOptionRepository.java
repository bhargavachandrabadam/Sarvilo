package com.easy.stazy.pgmanagement.pg.repository;

import com.easy.stazy.pgmanagement.pg.entities.RentalOptionEntity;
import com.easy.stazy.pgmanagement.pg.enums.RentalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for RentalOptionEntity, providing CRUD operations and queries for rental options linked to a PG.
 */
public interface RentalOptionRepository extends JpaRepository<RentalOptionEntity, Long> {
    List<RentalOptionEntity> findByPgId(Long pgId);

    RentalOptionEntity findByPgIdAndSharingTypeAndDurationType(long pgId, String sharingType, RentalType rentalType);

    @Query(value = "SELECT * FROM rental_options WHERE pg_id = :pgId AND sharing_type = :sharingType", nativeQuery = true)
    List<RentalOptionEntity> findByPgIdAndSharingTypeNative(@Param("pgId") Long pgId, @Param("sharingType") String sharingType);
}
