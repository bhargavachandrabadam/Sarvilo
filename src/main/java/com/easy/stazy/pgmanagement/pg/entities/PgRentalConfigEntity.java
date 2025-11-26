package com.easy.stazy.pgmanagement.pg.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pg_rental_config")
public class PgRentalConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pg_id", nullable = false)
    private PgManagementEntity pg;

    @Column(nullable = false)
    private String rentalType; // DAILY or MONTHLY

    @Column(nullable = false)
    private String sharingType; // e.g., "Single", "2 Sharing", etc.

    @Column(nullable = false)
    private Integer rentAmount;

    private Integer cautionDeposit; // nullable for daily rental

    private Boolean isActive;
}

