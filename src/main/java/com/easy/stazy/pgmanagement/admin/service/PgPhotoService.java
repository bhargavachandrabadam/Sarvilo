package com.easy.stazy.pgmanagement.admin.service;

import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PgPhotoService {
    List<String> uploadPgPhotos(List<MultipartFile> photos, long pgId, String uploadDir);

    void handlePgPhotos(PgManagementEntity pgManagementEntity, List<MultipartFile> newPhotos);
}

