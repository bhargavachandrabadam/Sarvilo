package com.easy.stazy.pgmanagement.pg.entities;


import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.shared.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "pg_details")
public class PgManagementEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;
    private String mobileNumber;
    private String pgName;
    private java.time.LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private PgOwnerEntity owner;
    private Boolean isOwnerManager;
    private String managerName;
    private String managerContactNumber;
    private String additionalContactNumber;
    private String state;
    private String district;
    private String pinCode;
    private String genderChoice;
    @Column(length = 1000)
    private String description;
    private Boolean dailyRentalActive;
    private Boolean monthlyRentalActive;
    private String upiAddress;
    private String gstin;
    private String bankAccountNumber;
    private String accountHolderName;
    private String ifsc;
    private String location;

    // Relationship to FloorEntity
    @OneToMany(mappedBy = "pg", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FloorEntity> floors;

    // Relationship to AmenityEntity
    @OneToMany(mappedBy = "pg", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AmenityEntity> amenities;

    // Relationship to RuleEntity
    @OneToMany(mappedBy = "pg", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RuleEntity> rules;

    // Relationship to RentalOptionEntity
    @OneToMany(mappedBy = "pg", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RentalOptionEntity> rentalOptions;

    // Store photo paths for PG images
    @ElementCollection
    @CollectionTable(name = "pg_photos", joinColumns = @JoinColumn(name = "pg_id"))
    @Column(name = "photo_path")
    private List<String> photoPaths;
}
