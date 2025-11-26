package com.easy.stazy.pgmanagement.owner.controller;

import com.easy.stazy.pgmanagement.tenant.service.TenantInfoService;
import com.easy.stazy.shared.common.util.SuccessResponseBody;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantCreateRequestDto;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantInfoResponseDto;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantInfoUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/owner/tenants")
@Tag(name = "Owner Tenant Management", description = "Owner-side tenant operations")
@RequiredArgsConstructor
public class OwnerTenantController {

    private final TenantInfoService tenantInfoService;

    @Operation(summary = "Create a new tenant")
    @PostMapping(path = "/{pgId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponseBody> createTenant(
            @ModelAttribute TenantCreateRequestDto tenantDto,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto, @RequestParam Long pgId) {
        tenantInfoService.createTenant(tenantDto, profilePhoto, pgId);
        return ResponseEntity.ok(new SuccessResponseBody("Tenant created successfully", null));
    }

    @Operation(summary = "Update tenant information")
    @PatchMapping(path = "/{tenantId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponseBody> updateTenantInfo(
            @PathVariable Long tenantId,
            @ModelAttribute TenantInfoUpdateRequestDto dto,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto) {

        tenantInfoService.updateTenant(tenantId, dto, profilePhoto);
        return ResponseEntity.ok(new SuccessResponseBody("Tenant info updated successfully", null));
    }

    @Operation(summary = "Get tenants by PG ID")
    @GetMapping("/pg/{pgId}")
    public ResponseEntity<SuccessResponseBody> getTenantsByPgId(@PathVariable Long pgId) {
        List<TenantInfoResponseDto> response = tenantInfoService.getTenantInfoByPgId(pgId);
        return ResponseEntity.ok(new SuccessResponseBody("Tenants fetched successfully", response));
    }

    @Operation(summary = "Get tenant info by tenant ID")
    @GetMapping("/{tenantId}")
    public ResponseEntity<SuccessResponseBody> getTenantInfoById(@PathVariable long tenantId) {
        TenantInfoResponseDto response = tenantInfoService.getTenantInfoById(tenantId);
        return ResponseEntity.ok(new SuccessResponseBody("Tenant info fetched successfully", response));
    }
}
