package com.easy.stazy.pgmanagement.admin.controller;

import com.easy.stazy.pgmanagement.admin.dto.request.PgManagementRequestDto;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantCreateRequestDto;
import com.easy.stazy.pgmanagement.pg.service.PgManagementService;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/admin")
public class EasyStazyAppController {


    private final PgManagementService easyStazyAppService;

    @GetMapping("/pglist")
    public List<PgManagementEntity> getPgList() {
        return easyStazyAppService.getPgList();
    }

    /**
     @Operation(summary = "Create PG", description = "Create new PG details to the system")
     @PostMapping("/addpglist") public ResponseEntity<String> addPgDetail(@RequestBody PgManagementEntity pgManagementRequest){
     PgManagementEntity pgManagement = easyStazyAppService.addPgDetail(pgManagementRequest);
     return ResponseEntity
     .status(HttpStatus.CREATED) // 201 Created
     .body("PG Details Added successfully with ID: " + pgManagement.getId());
     }
     **/
    /**
     * @PostMapping("/updatePgDetails/{id}") public ResponseEntity<String> updatePgDetails(@PathVariable Long id, @RequestBody PgManagementEntity pgManagement){
     * boolean update =  easyStazyAppService.updatePgDetails(id, pgManagement);
     * if(update)
     * return ResponseEntity.ok("PG Details Updated Successfully");
     * return ResponseEntity.notFound().build();
     * }
     **/


    @GetMapping("/health")
    public String healthCheck() {
        return "Stazy App is up and running!";
    }

    @Operation(summary = "Create PG with Optional Excel", description = "Create new PG details to the system with an optional Excel file for tenant import")
    @PostMapping(value = "/addpgwithExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addPgDetailWithOptionalExcel(
            @ModelAttribute PgManagementRequestDto pgManagementRequest,
            @ModelAttribute List<TenantCreateRequestDto> tenantCreateRequestDtos,
            @RequestPart(value = "excel", required = false) MultipartFile file,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            @RequestPart(value = "profilePhotos", required = false) List<MultipartFile> profilePhotos) {
        try {
            PgManagementEntity pgManagement = easyStazyAppService.addPgDetailWithExcelAndAudit(pgManagementRequest, photos, file, profilePhotos, tenantCreateRequestDtos);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("PG Details Added successfully with ID: " + pgManagement.getId() + (file != null && !file.isEmpty() ? ", and tenants imported from Excel." : "."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("PG creation failed: " + e.getMessage());
        }
    }
}
