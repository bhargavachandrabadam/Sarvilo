package com.easy.stazy.notifications.entity;

import com.easy.stazy.pgmanagement.pg.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "fcm_notification_log", indexes = {
        @Index(name = "idx_fcm_notification_log_identity_id_type", columnList = "identity_id, identity_type")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FcmNotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identity_id", nullable = false)
    private Long identityId;

    @Column(name = "identity_type", nullable = false, length = 20)
    private UserType identityType;

    @Column(name = "fcm_token", nullable = false, length = 255)
    private String fcmToken;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "body", nullable = false, length = 1000)
    private String body;

    @Column(name = "data_payload", columnDefinition = "TEXT")
    private String dataPayload;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "response_id", length = 255)
    private String responseId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
