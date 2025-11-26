package com.easy.stazy.pgmanagement.pg.controller;

import com.easy.stazy.pgmanagement.pg.service.impl.TenantExcelImportService;
import com.easy.stazy.pgmanagement.pg.service.impl.TenantExcelTemplateService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/excel")
public class ExcelController {

    private final TenantExcelTemplateService tenantExcelTemplateService;

    private final TenantExcelImportService tenantExcelImportService;

    @PostMapping("/tenant-import-excel")
    public ResponseEntity<?> importTenantsFromExcel(@RequestParam("excel") MultipartFile file, @RequestParam long pgId) {
        try {
            tenantExcelImportService.importTenantsAndCreateEntitiesFromExcel(file,pgId);
            return ResponseEntity.ok("Tenants imported successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to import tenants: " + e.getMessage());
        }
    }

    @GetMapping("/download-owner-excel")
    public void downloadOwnerExcel(HttpServletResponse response) throws IOException {

        // Path to the Excel file in the resources folder
        String fileName = "StayZy_Tenant_Template.xlsx";
        String resourcePath = "/" + fileName;

        // Set response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        // Read the file from resources and write to response
        try (var inputStream = this.getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found");
                return;
            }
            inputStream.transferTo(response.getOutputStream());
            response.flushBuffer();
        }
    }
}
