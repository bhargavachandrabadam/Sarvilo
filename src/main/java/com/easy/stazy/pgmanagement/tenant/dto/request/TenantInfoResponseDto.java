package com.easy.stazy.pgmanagement.tenant.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantInfoResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 5182801554461928366L;

    @Schema(example = "John Doe")
    private String tenantName;
    @Schema(example = "9876543210")
    private String contactNumber;
    @Schema(example = "john.doe@example.com")
    private String email;
    @Schema(example = "1")
    private String floor;
    @Schema(example = "101")
    private String roomNo;
    @Schema(example = "B1")
    private String bedNo;
    @Schema(example = "MONTHLY")
    private String rentalType;
    @Schema(example = "2023-01-01")
    private String joiningDate;
    @Schema(example = "123")
    private Long tenantId;
    @Schema(description = "List of tenant ID proofs and their types")
    private java.util.List<TenantProofDto> tenantIdProofs;
    @Schema(description = "Profile photo URL or path")
    private String profilePhoto;
}
