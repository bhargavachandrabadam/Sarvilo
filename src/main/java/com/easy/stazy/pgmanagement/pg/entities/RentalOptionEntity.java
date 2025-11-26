package com.easy.stazy.pgmanagement.pg.entities;

import com.easy.stazy.pgmanagement.pg.enums.RentalType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Entity representing a rental option for a PG, supporting daily and monthly pricing for different sharing types.
 */
@Getter
@Setter
@Entity
@Table(name = "rental_options")
public class RentalOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalType durationType; // DAILY or MONTHLY

    @Column(nullable = false)
    private String sharingType;  // SINGLE, 2 SHARING, etc.

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pg_id", nullable = false)
    private PgManagementEntity pg;

    @Column(nullable = false)
    private BigDecimal cautionDeposit;

    @ManyToMany(mappedBy = "rentalOptions")
    private List<RoomEntity> rooms;
}
