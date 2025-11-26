package com.easy.stazy.pgmanagement.pg.entities;

import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.shared.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "bed_occupancy")
public class BedOccupancyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id", nullable = false)
    private BedEntity bed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_info_id", nullable = false)
    private TenantInfoEntity tenant;

    private String status;
    private LocalDate joiningDate;
    private LocalDate exitDate;
    private String notes;

    @Column(name = "rental_type")
    private String rentalType;

    @Column(name = "sharing_type")
    private String sharingType;
}
