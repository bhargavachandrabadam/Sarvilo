package com.easy.stazy.reviews.repository;

import com.easy.stazy.reviews.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity,Long> {
    boolean existsByPgIdAndTenantId(long pgId, long tenantId);
    List<ReviewEntity> findByPgId(Long pgId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.pg.id = :pgId")
    long countByPgId(Long pgId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM ReviewEntity r WHERE r.pg.id = :pgId")
    Double avgRatingByPgId(Long pgId);

    @Query("SELECT r.pg.id, COALESCE(AVG(r.rating), 0) FROM ReviewEntity r WHERE r.pg.id IN :pgIds GROUP BY r.pg.id")
    List<Object[]> findAvgRatingsByPgIds(List<Long> pgIds);

    @Query("SELECT r.pg.id, COUNT(r) FROM ReviewEntity r WHERE r.pg.id IN :pgIds GROUP BY r.pg.id")
    List<Object[]> findCountsByPgIds(List<Long> pgIds);
}
