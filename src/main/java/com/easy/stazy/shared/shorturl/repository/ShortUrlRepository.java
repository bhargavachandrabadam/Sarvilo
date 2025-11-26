package com.easy.stazy.shared.shorturl.repository;

import com.easy.stazy.shared.shorturl.entities.ShortUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long> {
    Optional<ShortUrlEntity> findByCode(String code);
}

