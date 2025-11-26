package com.easy.stazy.pgmanagement.tenant.handlers;

import com.easy.stazy.pgmanagement.tenant.dto.request.TenantProofDto;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.pgmanagement.tenant.entities.TenantProofPhotoEntity;
import com.easy.stazy.photos.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class TenantPhotosHandler {

    private final FileStorageService fileStorageService;

    @Value("${tenant.photos.id-proof-dir}")
    private String proofPhotoUploadDir;

    @Value("${tenant.photos.profile-dir}")
    private String tenantPhotoUploadDir;

    public TenantPhotosHandler(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public void uploadProofPhotos(TenantInfoEntity tenant, List<TenantProofDto> tenantProofs) {
        if (tenantProofs == null || tenantProofs.isEmpty()) return;
        deleteOldProofPhotos(tenant);
        List<TenantProofPhotoEntity> newProofPhotos = uploadNewProofPhotos(tenant, tenantProofs);
        if (tenant.getProofPhotos() == null) {
            tenant.setProofPhotos(new ArrayList<>());
        }
        tenant.getProofPhotos().addAll(newProofPhotos);
    }

    private void deleteOldProofPhotos(TenantInfoEntity tenant) {
        if (tenant.getProofPhotos() != null) {
            Iterator<TenantProofPhotoEntity> iterator = tenant.getProofPhotos().iterator();
            while (iterator.hasNext()) {
                TenantProofPhotoEntity oldProof = iterator.next();
                if (oldProof.getProofPhotoUrl() != null) {
                    fileStorageService.deleteFile(oldProof.getProofPhotoUrl());
                }
                iterator.remove();
            }
        }
    }

    private List<TenantProofPhotoEntity> uploadNewProofPhotos(TenantInfoEntity tenant, List<TenantProofDto> tenantProofs) {
        List<TenantProofPhotoEntity> newProofPhotos = new ArrayList<>();
        for (TenantProofDto proofDto : tenantProofs) {
            MultipartFile proofPhotoFile = proofDto.getFile();
            String uploadedUrl = null;
            if (proofPhotoFile != null && !proofPhotoFile.isEmpty()) {
                String pathWithPgId = proofPhotoUploadDir + File.separator + tenant.getPg().getId() + File.separator + tenant.getId();
                List<String> urls = fileStorageService.uploadFile(List.of(proofPhotoFile), pathWithPgId);
                if (!urls.isEmpty()) {
                    uploadedUrl = urls.get(0);
                }
            }
            if (uploadedUrl != null) {
                TenantProofPhotoEntity proofPhotoEntity = new TenantProofPhotoEntity();
                proofPhotoEntity.setTenant(tenant);
                proofPhotoEntity.setProofPhotoUrl(uploadedUrl);
                proofPhotoEntity.setProofType(proofDto.getProofType());
                newProofPhotos.add(proofPhotoEntity);
            }
        }
        return newProofPhotos;
    }

    public void handleProfilePhoto(TenantInfoEntity tenant, MultipartFile profilePhoto) {
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            String existingPhotoUrl = tenant.getTenantPhotoUrl();
            if (tenant.getProofPhotos() == null) {
                tenant.setProofPhotos(new ArrayList<>());
            }
            if (existingPhotoUrl != null && !existingPhotoUrl.isEmpty()) {
                fileStorageService.deleteFile(existingPhotoUrl);
            }
            String pathWithPgId = tenantPhotoUploadDir + File.separator + tenant.getPg().getId() + File.separator + tenant.getId();
            List<String> profilePhotoPaths = fileStorageService.uploadFile(List.of(profilePhoto), pathWithPgId);
            if (!profilePhotoPaths.isEmpty()) {
                tenant.setTenantPhotoUrl(profilePhotoPaths.get(0));
            }
        }
    }

    public String moveProofPhotoToTenantFolder(String oldUrl, Long tenantId) {
        return fileStorageService.moveFileToTenantFolder(oldUrl, tenantId, proofPhotoUploadDir, "id-proof-photos");
    }

    public String moveProfilePhotoToTenantFolder(String oldUrl, Long tenantId) {
        return fileStorageService.moveFileToTenantFolder(oldUrl, tenantId, tenantPhotoUploadDir, "profile-photos");
    }
}
