package com.easy.stazy.shared.service;

import org.springframework.web.multipart.MultipartFile;

public interface AuditService {
    void storeAudit(String userName, String userId, Long pgId, MultipartFile file);
}

