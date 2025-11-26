package com.easy.stazy.pgmanagement.tenant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantProofResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 4651142430310306185L;
    private String proofType;
    private String proofPhotoUrl;
}

