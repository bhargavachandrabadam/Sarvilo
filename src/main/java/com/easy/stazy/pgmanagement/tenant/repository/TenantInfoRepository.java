package com.easy.stazy.pgmanagement.tenant.repository;

import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TenantInfoRepository extends JpaRepository<TenantInfoEntity, Long> {
    @Query(value = "SELECT u.name AS tenantName, u.phone_number AS contactNumber, u.email_id AS email, f.floor_number AS floor, r.room_number AS roomNo " +
            "FROM tenant_info t " +
            "JOIN users u ON t.user_id = u.id " +
            "JOIN bed_occupancy bo ON bo.tenant_info_id = t.id " +
            "JOIN beds b ON bo.bed_id = b.id " +
            "JOIN rooms r ON b.room_id = r.id " +
            "JOIN floors f ON r.floor_id = f.id " +
            "WHERE f.pg_id = :pgId", nativeQuery = true)
    List<Object[]> findTenantInfoByPgIdNative(@Param("pgId") Long pgId);

    List<TenantInfoEntity> findAllByUserId(Long userId);

    boolean existsByUser_IdAndPg_Id(Long userId, Long pgId);

    List<TenantInfoEntity> findByPgId(Long pgId);

    TenantInfoEntity findByUserId(Long id);

    @Query("SELECT t.user.id, t.pg.id FROM TenantInfoEntity t")
    List<Object[]> findAllUserIdAndPgId();

    List<TenantInfoEntity> findByUserIdIn(List<Long> userIds);

    Optional<TenantInfoEntity> findByUserIdAndPgId(Long id, @NotNull Long pgId);

    @Query("SELECT COUNT(b) > 0 FROM BedOccupancyEntity b WHERE b.tenant.id = :tenantId AND b.bed.room.floor.pg.id = :pgId AND (b.exitDate IS NULL OR b.status = 'ACTIVE')")
    boolean existsActiveOccupancyForUserAndPg(@Param("tenantId") Long tenantId, @Param("pgId") Long pgId);
}
