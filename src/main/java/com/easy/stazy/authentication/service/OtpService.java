package com.easy.stazy.authentication.service;

import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.authentication.entities.OtpEntity;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.authentication.repository.OtpRepository;
import com.easy.stazy.authentication.utils.OtpGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpRepository otpRepository;

    @Value("${otp.expiration.minutes}")
    private int otpExpirationMinutes;

    /**
     * Generates a unique OTP and creates or updates the OTP record for the user.
     * Returns the generated OTP value.
     */
    @Transactional
    public String generateAndSaveOtp(Object entity) {
        String otpValue = OtpGenerator.generateUniqueOtp(otpRepository);
        OtpEntity otpEntity = getOtpEntity(entity);
        if (otpEntity != null) {
            updateOtpEntity(otpEntity, otpValue);
        } else {
            otpEntity = buildOtpEntity(entity, otpValue);
            otpRepository.save(otpEntity);
        }
        return otpValue;
    }

    private void updateOtpEntity(OtpEntity otpEntity, String otpValue) {
        otpEntity.setOtp(otpValue);
        otpEntity.setIsActive(true);
        otpEntity.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        otpRepository.save(otpEntity);
    }

    private OtpEntity getOtpEntity(Object entity) {
        if (entity instanceof UsersEntity user) {
            return otpRepository.findByUser(user);
        } else if (entity instanceof PgOwnerEntity pgOwner) {
            return otpRepository.findByPgOwner(pgOwner);
        }
        return null;
    }

    private OtpEntity buildOtpEntity(Object entity, String otpValue) {
        if (entity instanceof UsersEntity user) {
            return OtpEntity.builder()
                    .otp(otpValue)
                    .isActive(true)
                    .expiresAt(java.time.LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                    .user(user)
                    .build();
        } else if (entity instanceof PgOwnerEntity pgOwner) {
            return OtpEntity.builder()
                    .otp(otpValue)
                    .isActive(true)
                    .expiresAt(java.time.LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                    .pgOwner(pgOwner)
                    .build();
        }
        throw new IllegalArgumentException("Unsupported entity type for OTP generation");
    }

    /**
     * Returns the active OTP entity for the given user, or null if none exists.
     */
    public OtpEntity getActiveOtpForUser(UsersEntity user) {
        return otpRepository.findByUserAndIsActive(user, true).orElse(null);
    }

    public OtpEntity getActiveOtpForPgOwner(PgOwnerEntity pgOwner) {
        return otpRepository.findByPgOwnerAndIsActive(pgOwner, true).orElse(null);
    }

    /**
     * Deactivates the given OTP entity (sets isActive to false and saves it).
     */
    public void deactivateOtp(OtpEntity otpEntity) {
        otpEntity.setIsActive(false);
        otpRepository.save(otpEntity);
    }
}
