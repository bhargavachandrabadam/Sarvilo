package com.easy.stazy.pgmanagement.tenant.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantInfoUpdateRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -5701365139498713008L;
    @Schema(example = "John Doe")
    private String name;
    @Schema(example = "9876543210")
    private String contactNumber;
    @Schema(example = "john.doe@example.com")
    private String emailId;
    @Schema(example = "1")
    private String floorNumber;
    @Schema(example = "101")
    private String roomNumber;
    @Schema(example = "A2")
    private String bedNumber;
    @Schema(example = "MONTHLY")
    private String rentalType;
    @Schema(example = "2 Sharing")
    private String sharingType;
    private List<TenantProofDto> tenantProofs;

}
