package com.easy.stazy.authentication.repository;

import com.easy.stazy.authentication.entities.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity,Long> {
    void deleteAllByUserId(Long userId);

    Optional<TokenEntity> findByRefreshToken(String refreshToken);
}
