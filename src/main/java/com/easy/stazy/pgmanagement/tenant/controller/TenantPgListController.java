package com.easy.stazy.pgmanagement.tenant.controller;

import com.easy.stazy.pgmanagement.pg.dto.response.PgUserListResponseDto;
import com.easy.stazy.pgmanagement.tenant.service.TenantInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tenant")
@Tag(name = "Tenant PG List", description = "APIs for tenants to view their joined PGs with favorite status")
@RequiredArgsConstructor
public class TenantPgListController {
    private final TenantInfoService tenantInfoService;

    /**
     * Get list of PGs for a user with favorite status and details.
     * Only accessible for users with USER role.
     */
    @Operation(summary = "Get list of PGs joined by the tenant", description = "Returns PGs the tenant has joined, with favorite status and details.")
    @GetMapping("/user/{userId}/pgs-list")
    public ResponseEntity<List<PgUserListResponseDto>> getPgsListForUser(@PathVariable Long userId) {
        List<PgUserListResponseDto> result = tenantInfoService.getPgsListForUser(userId);
        return ResponseEntity.ok(result);
    }
}
