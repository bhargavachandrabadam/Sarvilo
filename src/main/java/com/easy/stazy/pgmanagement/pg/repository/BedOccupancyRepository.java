package com.easy.stazy.pgmanagement.pg.repository;

import com.easy.stazy.pgmanagement.pg.entities.BedEntity;
import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BedOccupancyRepository extends JpaRepository<BedOccupancyEntity, Long> {
    boolean existsByBed_IdAndStatus(Long bedId, String status);

    // Count active/occupied beds for a given PG. Treat occupancy as active when exitDate is null or status = 'ACTIVE'
    @Query("SELECT COUNT(bc) FROM BedOccupancyEntity bc JOIN bc.bed b JOIN b.room r JOIN r.floor f JOIN f.pg p " +
            "WHERE p.id = :pgId AND (bc.exitDate IS NULL OR bc.status = 'ACTIVE')")
    long countActiveByPgId(Long pgId);

    List<BedOccupancyEntity> findByStatus(String name);

    List<BedOccupancyEntity> findByTenantAndStatus(TenantInfoEntity tenant, String string);

    Optional<Object> findFirstByTenantAndStatus(TenantInfoEntity tenant, String string);

    // Returns the most recent (latest) ACTIVE bed occupancy for a tenant
    Optional<BedOccupancyEntity> findTopByTenantAndStatusOrderByJoiningDateDesc(TenantInfoEntity tenant, String status);

    // Returns the most recent (latest) ACTIVE bed occupancy for a tenant and specific bed
    Optional<BedOccupancyEntity> findTopByBedAndStatusOrderByJoiningDateDesc(BedEntity bed, String status);

    @Query("SELECT bo FROM BedOccupancyEntity bo WHERE bo.bed.id = :bedId " +
           "AND (bo.joiningDate <= :endOfMonth AND (bo.exitDate IS NULL OR bo.exitDate >= :startOfMonth)) " +
           "AND bo.status = 'ACTIVE'")
    BedOccupancyEntity findActiveByBedIdAndMonth(
        @Param("bedId") Long bedId,
        @Param("startOfMonth") LocalDate startOfMonth,
        @Param("endOfMonth") LocalDate endOfMonth
    );

    // Bulk: active occupancies for many beds within a month
    @Query("SELECT bo FROM BedOccupancyEntity bo " +
           "JOIN FETCH bo.tenant t " +
           "JOIN FETCH t.user u " +
           "WHERE bo.bed.id IN :bedIds " +
           "AND (bo.joiningDate <= :endOfMonth AND (bo.exitDate IS NULL OR bo.exitDate >= :startOfMonth)) " +
           "AND bo.status = 'ACTIVE'")
    List<BedOccupancyEntity> findActiveByBedIdInAndMonth(
        @Param("bedIds") List<Long> bedIds,
        @Param("startOfMonth") LocalDate startOfMonth,
        @Param("endOfMonth") LocalDate endOfMonth
    );

    List<BedOccupancyEntity> findActiveByTenantIdIn(List<Long> tenantIds);


    @Query("SELECT bo FROM BedOccupancyEntity bo " +
           "JOIN FETCH bo.tenant t " +
           "JOIN FETCH t.user u " +
           "WHERE bo.bed.id IN :bedIds " +
           "AND bo.joiningDate <= :selectedDate " +
           "AND (bo.exitDate IS NULL OR bo.exitDate > :selectedDate)")
    List<BedOccupancyEntity> findByBedIdInAndDate(List<Long> bedIds, LocalDate selectedDate);


    @Query(
      value = "SELECT DISTINCT EXTRACT(YEAR FROM bo.joining_date), EXTRACT(MONTH FROM bo.joining_date) " +
              "FROM bed_occupancy bo " +
              "JOIN beds b ON b.id = bo.bed_id " +
              "JOIN rooms r ON r.id = b.room_id " +
              "JOIN floors f ON f.id = r.floor_id " +
              "WHERE f.pg_id = :pgId " +
              "ORDER BY EXTRACT(YEAR FROM bo.joining_date) DESC, EXTRACT(MONTH FROM bo.joining_date) DESC",
      nativeQuery = true
    )
    List<Object[]> findDistinctYearMonthsByPgId(@Param("pgId") Long pgId);

    @Query("SELECT bo FROM BedOccupancyEntity bo " +
            "JOIN FETCH bo.tenant t JOIN FETCH t.user WHERE bo.joiningDate = :joiningDate " +
            "AND bo.status = :status")
    List<BedOccupancyEntity> findByJoiningDateAndStatus(@Param("joiningDate") LocalDate joiningDate, @Param("status") String status);

    Optional<BedOccupancyEntity> findByTenant_IdAndStatus(Long tenantId, String status);

    List<BedOccupancyEntity> findByBedIdIn(List<Long> bedIds);
}