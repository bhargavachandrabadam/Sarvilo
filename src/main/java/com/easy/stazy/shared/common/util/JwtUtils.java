package com.easy.stazy.shared.common.util;

import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.authentication.dto.TokenDto;
import com.easy.stazy.authentication.entities.TokenEntity;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.authentication.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "app.security.jwt")
@Getter
@Setter
@RequiredArgsConstructor
public class JwtUtils {
    private final TokenRepository tokenRepository;
    private String secret;
    private long expirationMs;
    private long refreshTokenExpirationDays;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured");
        }
    }


    public TokenDto generateToken(UsersEntity usersEntity) {
        var now = new Date();
        var expiry = new Date(now.getTime() + expirationMs);
        String refreshToken = saveRefreshToken(usersEntity);
        String jwt = Jwts.builder()
                .setClaims(Map.of(
                        "phoneNumber", usersEntity.getPhoneNumber()))
                .setSubject(usersEntity.getPhoneNumber())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder().
                token(jwt).
                refreshToken(refreshToken).
                build();
    }

    public TokenDto generateOwnerToken(PgOwnerEntity pgOwnerEntity) {
        var now = new Date();
        var expiry = new Date(now.getTime() + expirationMs);
        String refreshToken = saveRefreshToken(pgOwnerEntity);
        String jwt = Jwts.builder()
                .setClaims(Map.of(
                        "phoneNumber", pgOwnerEntity.getOwnerContactNumber()))
                .setSubject(pgOwnerEntity.getOwnerContactNumber())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder().
                token(jwt).
                refreshToken(refreshToken).
                build();
    }

    private String saveRefreshToken(PgOwnerEntity pgOwnerEntity) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setOwnerId(pgOwnerEntity.getId());
        tokenEntity.setRefreshToken(generateRefreshToken());
        tokenEntity.setExpiryDate(LocalDateTime.now().plusDays(refreshTokenExpirationDays));
        tokenRepository.save(tokenEntity);
        return tokenEntity.getRefreshToken();
    }

    private String saveRefreshToken(UsersEntity usersEntity) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUserId(usersEntity.getId());
        tokenEntity.setRefreshToken(generateRefreshToken());
        tokenEntity.setExpiryDate(LocalDateTime.now().plusDays(refreshTokenExpirationDays));
        tokenRepository.save(tokenEntity);
        return tokenEntity.getRefreshToken();
    }


    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }


}
