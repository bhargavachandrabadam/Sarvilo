package com.easy.stazy.pgmanagement.admin.service;

import org.springframework.web.multipart.MultipartFile;

public interface TenantImportService {
    void importTenantsAndCreateEntitiesFromExcel(MultipartFile file, Long pgId) throws Exception;
}

