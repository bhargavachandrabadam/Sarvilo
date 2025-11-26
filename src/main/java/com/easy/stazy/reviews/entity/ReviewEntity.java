package com.easy.stazy.reviews.entity;

import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.shared.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class ReviewEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "pg_id")
    private PgManagementEntity pg;

    @Column(length = 1000)
    private String description;

    private Double rating;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private TenantInfoEntity tenant;
}
