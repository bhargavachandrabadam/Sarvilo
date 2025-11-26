package com.easy.stazy.pgmanagement.pg.controller;

import com.easy.stazy.pgmanagement.pg.dto.response.PgDetailsWithPhotosResponseDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgFilterResponseDto;
import com.easy.stazy.pgmanagement.pg.dto.request.BedOccupancyUpdateRequestDto;
import com.easy.stazy.pgmanagement.pg.service.BedService;
import com.easy.stazy.pgmanagement.pg.service.PgManagementService;
import com.easy.stazy.reviews.service.ReviewService;
import com.easy.stazy.shared.common.util.SuccessResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/common")
@Tag(name = "Common APIs", description = "Shared APIs for PG details, reviews, and listings and exit date updates")
@RequiredArgsConstructor
public class CommonController {
    private final PgManagementService pgManagementService;
    private final ReviewService reviewService;
    private final BedService bedService;

    @Operation(summary = "Get PG Details by ID")
    @GetMapping("/pg/{pgId}")
    public ResponseEntity<PgDetailsWithPhotosResponseDto> getPgDetails(@PathVariable Long pgId) {
        PgDetailsWithPhotosResponseDto response = pgManagementService.getPgDetailsById(pgId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Paginated PG Details")
    @GetMapping("/pg-list/page")
    public ResponseEntity<Page<PgFilterResponseDto>> getPaginatedPgs(Pageable pageable) {
        Page<PgFilterResponseDto> page = pgManagementService.getPgDetails(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get Reviews by PG ID")
    @GetMapping("/reviews/{pgId}")
    public ResponseEntity<SuccessResponseBody> getReviewsByPgId(@PathVariable Long pgId) {
        return ResponseEntity.ok(new SuccessResponseBody("Reviews fetched successfully", reviewService.getReviewsByPgId(pgId)));
    }

    @Operation(summary = "Update bed occupancy exit date", description = "Updates the exit date for a specific bed occupancy record.")
    @PatchMapping("/bed-occupancy/exit-date/{tenantId}")
    public ResponseEntity<SuccessResponseBody> updateBedOccupancy( @PathVariable Long tenantId, @Valid @RequestBody BedOccupancyUpdateRequestDto request) {
        bedService.updateExitDate(tenantId, request);
        return ResponseEntity.ok(new SuccessResponseBody("Bed occupancy updated successfully", null));
    }

    @Operation(summary = "Delete bed occupancy exit date", description = "Deletes the exit date for a specific bed occupancy record.")
    @DeleteMapping("/bed-occupancy/exit-date/{tenantId}")
    public ResponseEntity<SuccessResponseBody> deleteBedOccupancyExitDate(@PathVariable Long tenantId) {
        bedService.deleteExitDate(tenantId);
        return ResponseEntity.ok(new SuccessResponseBody("Bed occupancy exit date deleted successfully", null));
    }

}
