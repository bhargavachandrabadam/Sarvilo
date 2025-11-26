package com.easy.stazy.complaints.repository;

import com.easy.stazy.complaints.entity.ComplaintEntity;
import com.easy.stazy.complaints.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<ComplaintEntity, Long> {


    @Query("SELECT c FROM ComplaintEntity c " +
           "LEFT JOIN FETCH c.tenant t " +
           "LEFT JOIN FETCH t.user u " +
           "LEFT JOIN FETCH c.floor f " +
           "LEFT JOIN FETCH c.room r " +
           "LEFT JOIN FETCH c.bed b " +
           "WHERE c.pg.id = :pg_id")
    List<ComplaintEntity> findByPg(@Param("pg_id") Long pgId);

    @Query("SELECT c FROM ComplaintEntity c " +
           "LEFT JOIN FETCH c.tenant t " +
           "LEFT JOIN FETCH t.user u " +
           "LEFT JOIN FETCH c.floor f " +
           "LEFT JOIN FETCH c.room r " +
           "LEFT JOIN FETCH c.bed b " +
           "WHERE c.pg.id = :pg_id AND c.tenant.id = :tenant_id")
    List<ComplaintEntity> findByPgIdAndTenantId(@Param("pg_id") Long pgId, @Param("tenant_id") Long tenantId);

    @Query("SELECT c FROM ComplaintEntity c " +
           "LEFT JOIN FETCH c.tenant t " +
           "LEFT JOIN FETCH t.user u " +
           "LEFT JOIN FETCH c.floor f " +
           "LEFT JOIN FETCH c.room r " +
           "LEFT JOIN FETCH c.bed b " +
           "WHERE c.pg.id = :pg_id AND c.tenant.id = :tenant_id AND c.status = :status")
    List<ComplaintEntity> findByPgIdAndTenantIdAndStatus(@Param("pg_id") Long pgId, @Param("tenant_id") Long tenantId, @Param("status") ComplaintStatus status);

    @Query("SELECT c FROM ComplaintEntity c " +
           "LEFT JOIN FETCH c.tenant t " +
           "LEFT JOIN FETCH t.user u " +
           "LEFT JOIN FETCH c.floor f " +
           "LEFT JOIN FETCH c.room r " +
           "LEFT JOIN FETCH c.bed b " +
           "WHERE c.pg.id = :pgId AND c.status = :status")
    List<ComplaintEntity> findByPgAndStatus(@Param("pgId") long pgId, @Param("status") ComplaintStatus status);
}
