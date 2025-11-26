package com.easy.stazy.pgmanagement.admin.service.impl;

import com.easy.stazy.pgmanagement.admin.service.PgAuditService;
import com.easy.stazy.shared.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PgAuditServiceImpl implements PgAuditService {
    private final AuditService auditService;

    public PgAuditServiceImpl(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void storeAudit(String userName, String userId, Long pgId, MultipartFile file) {
        auditService.storeAudit(userName, userId, pgId, file);
    }
}

