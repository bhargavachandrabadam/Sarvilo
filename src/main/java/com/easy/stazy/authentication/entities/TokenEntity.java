package com.easy.stazy.authentication.entities;

import com.easy.stazy.shared.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity representing a JWT or refresh token for a user.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Setter
@Getter
@Entity
@Table(name = "tokens")
public class TokenEntity extends BaseEntity {

    /**
     * Unique identifier for the token (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    /**
     * Reference to the user or owner who owns this token.
     */
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "owner_id")
    private Long ownerId;

    /**
     * The refresh token string.
     */
    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    /**
     * The expiry date/time of the token.
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}
