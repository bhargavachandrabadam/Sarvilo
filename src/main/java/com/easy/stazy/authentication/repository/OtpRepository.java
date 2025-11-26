package com.easy.stazy.authentication.repository;

import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.authentication.entities.OtpEntity;
import com.easy.stazy.authentication.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity> findByOtpAndIsActive(String otp, Boolean isActive);

    Optional<OtpEntity> findByUserAndIsActive(UsersEntity user, Boolean isActive);

    OtpEntity findByUser(UsersEntity user);

    OtpEntity findByPgOwner(PgOwnerEntity pgOwner);

    Optional<OtpEntity> findByPgOwnerAndIsActive(PgOwnerEntity pgOwner, boolean b);
}

