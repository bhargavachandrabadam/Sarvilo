package com.easy.stazy.pgmanagement.owner.entities;

import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.shared.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pg_owners")
public class PgOwnerEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "owner_contact_number", nullable = false, unique = true)
    private String ownerContactNumber;

    @Column(name = "owner_email_address", nullable = false, unique = true)
    private String ownerEmailAddress;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PgManagementEntity> pgs;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "is_active")
    private Boolean isActive;

//    @Column(name = "upi_address")
//    private String upiAddress;
//
//    @Column(name = "gstin")
//    private String gstin;
//
//    @Column(name = "bank_account_number")
//    private String bankAccountNumber;
//
//    @Column(name = "account_holder_name")
//    private String accountHolderName;
//
//    @Column(name = "ifsc")
//    private String ifsc;
}
