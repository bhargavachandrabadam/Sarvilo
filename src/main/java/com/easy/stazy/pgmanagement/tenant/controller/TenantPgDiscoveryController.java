package com.easy.stazy.pgmanagement.tenant.controller;
import com.easy.stazy.pgmanagement.pg.dto.response.PgFilterResponseDto;
import com.easy.stazy.pgmanagement.pg.service.PgManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tenant/pgs")
@Tag(name = "Tenant PG Discovery", description = "Tenant-side PG search and listing")
@RequiredArgsConstructor
public class TenantPgDiscoveryController {

    private final PgManagementService easyStazyAppService;

    @Operation(summary = "Get featured PGs")
    @GetMapping("/featured")
    public Page<PgFilterResponseDto> getFeaturedPgs(
            @RequestParam(required = false) String location,
            Pageable pageable) {

        return easyStazyAppService.getFeaturedPgs(location, pageable);
    }

    @Operation(summary = "Filter PGs")
    @GetMapping("/filter")
    public ResponseEntity<List<PgFilterResponseDto>> filterPgs(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> amenities,
            @RequestParam(required = false) List<String> sharingType,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) List<String> places) {

        List<PgFilterResponseDto> result = easyStazyAppService.filterPgs(minPrice, maxPrice, amenities, sharingType, gender, places);
        return ResponseEntity.ok(result);
    }
}
