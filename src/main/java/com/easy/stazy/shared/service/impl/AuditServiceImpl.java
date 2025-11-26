package com.easy.stazy.shared.service.impl;

import com.easy.stazy.shared.service.AuditService;
import com.easy.stazy.shared.entities.AuditEntity;
import com.easy.stazy.photos.FileStorageService;
import com.easy.stazy.shared.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    @Value("${tenant.files.file-dir}")
    private String fileDir;

    private final AuditRepository auditRepository;
    private final FileStorageService fileStorageService;


    @Transactional
    @Override
    public void storeAudit(String userName, String userId, Long pgId, MultipartFile file) {
        String pgDir = fileDir + File.separator + pgId;
        List<String> filePaths = fileStorageService.uploadFile(List.of(file), pgDir);
        AuditEntity auditEntity = new AuditEntity();
        auditEntity.setFilePath(filePaths.isEmpty() ? null : filePaths.get(0));
        auditEntity.setPgId(pgId);
        auditEntity.setUserId(userId);
        auditEntity.setUserName(userName);
        auditRepository.save(auditEntity);
    }
}
