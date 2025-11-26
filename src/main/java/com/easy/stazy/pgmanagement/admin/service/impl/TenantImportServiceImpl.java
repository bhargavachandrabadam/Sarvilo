package com.easy.stazy.pgmanagement.admin.service.impl;

import com.easy.stazy.pgmanagement.admin.service.TenantImportService;
import com.easy.stazy.pgmanagement.pg.service.impl.TenantExcelImportService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service implementation for importing tenants using various forms of input.
 * <p>
 * This class delegates the actual Excel import logic to {@link TenantExcelImportService}.
 * The reason for maintaining a separate TenantExcelImportService is to keep the Excel-specific
 * parsing, validation, and entity creation logic modular and reusable. This separation allows
 * the system to support multiple import mechanisms (such as Excel, CSV, or API-based bulk import)
 * in the future, while keeping the core import orchestration logic clean and maintainable.
 * <p>
 * By using this approach, the application can easily extend or modify import strategies without
 * affecting the main service interface, promoting better separation of concerns and testability.
 */
@Service
public class TenantImportServiceImpl implements TenantImportService {
    private final TenantExcelImportService tenantExcelImportService;

    public TenantImportServiceImpl(TenantExcelImportService tenantExcelImportService) {
        this.tenantExcelImportService = tenantExcelImportService;
    }

    @Override
    public void importTenantsAndCreateEntitiesFromExcel(MultipartFile file, Long pgId) throws Exception {
        tenantExcelImportService.importTenantsAndCreateEntitiesFromExcel(file, pgId);
    }
}
