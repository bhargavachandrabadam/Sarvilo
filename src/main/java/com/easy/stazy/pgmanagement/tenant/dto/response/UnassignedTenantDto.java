package com.easy.stazy.pgmanagement.tenant.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UnassignedTenantDto {
    private Long tenantId;
    private String tenantName;
    private String tenantPhone;
    private String tenantEmail;
    private LocalDate approvedAt;
    private String rentalType;
    private String sharingType;
}

