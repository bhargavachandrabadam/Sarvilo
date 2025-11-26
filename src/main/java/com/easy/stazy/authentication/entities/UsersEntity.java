package com.easy.stazy.authentication.entities;


import com.easy.stazy.shared.entities.BaseEntity;
import com.easy.stazy.authentication.dto.RoleType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a user in the system.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Setter
@Getter
@Entity
@Table(name = "users")
public class UsersEntity extends BaseEntity {

    /**
     * Unique identifier for the user (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Hashed password for user authentication.
     */
    @Column(name = "password_hash")
    private String passwordHash;

    /**
     * User's mobile phone number (must be unique).
     */
    @Column(name="phone_number",nullable=false,unique = true)
    private String phoneNumber;

    /**
     * Role of the user (e.g., OWNER, TENANT).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    /**
     * Indicates if the user account is active.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * User's email address.
     */
    @Column(name = "email_id")
    private String emailId;

    /**
     * Username for the user (can be used for login or display).
     */
    @Column(name = "name")
    private String name;

    /**
     * Indicates if the user has opted in for in-app payments.
     */
    @Column(name = "in_app_payment_opt_in")
    private Boolean inAppPaymentOptIn;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

}
