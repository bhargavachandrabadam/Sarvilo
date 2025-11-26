package com.easy.stazy.pgmanagement.tenant.dto.request;

import lombok.Data;

@Data
public class TenantExcelRowDto {
    private String tenantName;
    private String contactNumber;
    private String email;
    private String floor;
    private String roomNo;
    private String bedNo;
    private String sharingType;
    private String idProofType;
    private String notes;
}
