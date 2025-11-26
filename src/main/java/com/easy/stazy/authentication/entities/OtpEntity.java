package com.easy.stazy.authentication.entities;

import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.shared.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity class representing a One-Time Password (OTP) record for users.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Setter
@Getter
@Entity
@Table(name = "otps")
public class OtpEntity extends BaseEntity {

    /**
     * Unique identifier for the OTP record (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The 4-digit OTP value.
     */
    @Column(name = "otp", length = 4, nullable = false)
    private String otp;

    /**
     * Indicates if the OTP is currently active.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * The expiration timestamp for the OTP.
     * This field must be set when creating an OTP and is always required.
     * It ensures OTPs cannot be used indefinitely and supports secure deletion after verification.
     */
    @Column(name = "expires_at", nullable = false)
    private java.time.LocalDateTime expiresAt;

    /**
     * The user associated with this OTP (one-to-one relationship).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UsersEntity user;

    /**
     * The PG owner associated with this OTP (one-to-one relationship).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pg_owner_id")
    private PgOwnerEntity pgOwner;
}
