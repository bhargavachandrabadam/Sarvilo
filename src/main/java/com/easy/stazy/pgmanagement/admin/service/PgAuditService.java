package com.easy.stazy.pgmanagement.admin.service;

import org.springframework.web.multipart.MultipartFile;

public interface PgAuditService {
    void storeAudit(String userName, String userId, Long pgId, MultipartFile file);
}

