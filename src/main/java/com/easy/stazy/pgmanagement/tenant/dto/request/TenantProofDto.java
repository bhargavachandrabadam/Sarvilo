package com.easy.stazy.pgmanagement.tenant.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantProofDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 4651142430310306185L;
    private String proofType;
    private MultipartFile file;
}
