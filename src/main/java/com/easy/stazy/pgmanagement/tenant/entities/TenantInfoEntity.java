package com.easy.stazy.pgmanagement.tenant.entities;

import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.shared.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tenant_info")
public class TenantInfoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TenantProofPhotoEntity> proofPhotos = new java.util.ArrayList<>();

    // S3 URL for tenant photo
    @Column(name = "tenant_photo_url")
    private String tenantPhotoUrl;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BedOccupancyEntity> bedOccupancies;

    //It is used for any additional notes regarding the tenant
    @Column(name="notes")
    private String notes;

    // Nullable reference to PG in db level
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pg_id")
    private PgManagementEntity pg;

}
