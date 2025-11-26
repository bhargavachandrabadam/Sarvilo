package com.easy.stazy.notifications.entity;

import com.easy.stazy.pgmanagement.pg.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "fcm_device_token", indexes = {
        @Index(name = "idx_identity_id_type", columnList = "identity_id, identity_type")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FcmDeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identity_id", nullable = false)
    private Long identityId;

    @Column(name = "identity_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserType identityType; // Consider using Enum for type safety

    @Column(name = "fcm_token", nullable = false, length = 255)
    private String fcmToken;

    @Column(name = "device_type", nullable = false, length = 20)
    private String deviceType;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

}


