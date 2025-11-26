package com.easy.stazy.pgmanagement.owner.controller;

import com.easy.stazy.pgmanagement.admin.dto.request.PgManagementRequestDto;
import com.easy.stazy.pgmanagement.admin.service.OwnerPgService;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantCreateRequestDto;
import com.easy.stazy.pgmanagement.pg.dto.response.OwnerPgResponseDto;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.service.PgManagementService;
import com.easy.stazy.shared.common.util.SuccessResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Owner Controller", description = "APIs for Owner to manage their PGs")
@RestController
@RequestMapping("/v1/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerPgService ownerPgService;

    private final PgManagementService easyStazyAppService;

    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private String maxFileSize;
    @Value("${spring.servlet.multipart.max-request-size:10MB}")
    private String maxRequestSize;

    @PostConstruct
    public void logMultipartConfig() {
        System.out.println("[INFO] Max file size: " + maxFileSize);
        System.out.println("[INFO] Max request size: " + maxRequestSize);
    }

    @GetMapping("/{ownerId}/pgs")
    public ResponseEntity<SuccessResponseBody> getPgsByOwner(@PathVariable Long ownerId) {
        List<OwnerPgResponseDto> result = ownerPgService.getPgsByOwner(ownerId);
        return ResponseEntity.ok(new SuccessResponseBody("PGs fetched successfully", result));
    }

    @Operation(summary = "Update PG Details", description = "Updates PG details for the specified ID")
    @PutMapping(value = "/updatePgDetails/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updatePgDetailsWithDto(
            @PathVariable Long id,
            @ModelAttribute PgManagementRequestDto pgManagementRequest,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        easyStazyAppService.updatePgDetails(id, pgManagementRequest, photos);
        return ResponseEntity.ok("PG Details Updated Successfully");
    }

    @DeleteMapping("/rental-option/{id}")
    @Operation(summary = "Delete Rental Option by ID", description = "Deletes a rental option by its ID")
    public ResponseEntity<Void> deleteRentalOption(@PathVariable Long id) {
        easyStazyAppService.deleteRentalOptionById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create PG with Optional Excel",
            description = "Create new PG details to the system with an optional Excel file for tenant import")
    @PostMapping(value = "/addpgwithExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addPgDetailWithOptionalExcel(
            @Valid @RequestPart("pgManagementRequest") PgManagementRequestDto pgManagementRequest,
            @RequestPart(value = "tenants", required = false) List<TenantCreateRequestDto> tenants,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos,
            @RequestParam(value = "profilePhotos", required = false) List<MultipartFile> profilePhotos) {
        try {
            PgManagementEntity pgManagement = easyStazyAppService.addPgDetailWithExcelAndAudit(
                    pgManagementRequest,
                    photos != null ? photos : List.of(),
                    file,
                    profilePhotos != null ? profilePhotos : List.of(),
                    tenants != null ? tenants : List.of()
            );

            String message = "PG Details Added successfully with ID: " + pgManagement.getId();
            if (file != null && !file.isEmpty()) {
                message += ", and tenants imported from Excel.";
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(message);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("PG creation failed: " + e.getMessage());
        }
    }
}
