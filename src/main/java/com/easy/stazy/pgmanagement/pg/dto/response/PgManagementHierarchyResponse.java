package com.easy.stazy.pgmanagement.pg.dto.response;

import com.easy.stazy.pgmanagement.pg.dto.PgHierarchyResponse;
import com.easy.stazy.pgmanagement.tenant.dto.response.UnassignedTenantDto;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PgManagementHierarchyResponse {
    private PgHierarchyResponse pgHierarchy;
    private List<UnassignedTenantDto> unassignedTenantDtos;
    private LocalDate selectedMonth;
}

