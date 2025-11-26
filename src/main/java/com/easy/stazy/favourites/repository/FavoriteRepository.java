package com.easy.stazy.favourites.repository;

import com.easy.stazy.favourites.entity.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {
    List<FavoriteEntity> findByUserId(Long userId);
    Optional<FavoriteEntity> findByUserIdAndPgId(Long userId, Long pgId);
    void deleteByUserIdAndPgId(Long userId, Long pgId);

    List<Long> findPgIdsByUserId(Long userId);
}

