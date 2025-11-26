package com.easy.stazy.pgmanagement.tenant.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tenant_proof_photos")
public class TenantProofPhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantInfoEntity tenant;

    @Column(name = "proof_type", nullable = false)
    private String proofType;

    @Column(name = "proof_photo_url", nullable = false)
    private String proofPhotoUrl;

}

