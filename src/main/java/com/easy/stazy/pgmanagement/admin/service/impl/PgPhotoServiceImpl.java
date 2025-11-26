package com.easy.stazy.pgmanagement.admin.service.impl;

import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.pgmanagement.admin.service.PgPhotoService;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.photos.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service implementation for handling PG (Paying Guest) photo operations.
 */
@Service
@RequiredArgsConstructor
public class PgPhotoServiceImpl implements PgPhotoService {
    private final FileStorageService fileStorageService;

    private final PgManagementRepository pgRepository;

    @Value("${pg.photos.upload-dir}")
    private String uploadDir;

    /**
     * Uploads photos for a specific PG and returns the list of uploaded file paths.
     *
     * @param photos            List of MultipartFile objects representing the photos to upload.
     * @param pgId              The ID of the PG for which photos are being uploaded.
     * @param uploadDirOverride Optional directory path to override the default upload directory.
     * @return List of file paths where the photos are uploaded.
     */
    @Transactional
    @Override
    public List<String> uploadPgPhotos(List<MultipartFile> photos, long pgId, String uploadDirOverride) {
        String dir = uploadDirOverride != null ? uploadDirOverride : uploadDir + java.io.File.separator + pgId + java.io.File.separator;
        return fileStorageService.uploadFile(photos, dir);
    }

    /**
     * Handles the update of PG photos. Deletes old photos and uploads new ones for the given PG entity.
     *
     * @param pgManagementEntity The PG management entity whose photos are to be updated.
     * @param newPhotos          List of new MultipartFile photos to upload. If null or empty, no new photos are uploaded.
     */
    @Override
    public void handlePgPhotos(PgManagementEntity pgManagementEntity, List<MultipartFile> newPhotos) {
        if (pgManagementEntity.getPhotoPaths() != null) {
            for (String oldPhotoUrl : pgManagementEntity.getPhotoPaths()) {
                fileStorageService.deleteFile(oldPhotoUrl);
            }
            pgManagementEntity.getPhotoPaths().clear();
        }
        if (newPhotos == null || newPhotos.isEmpty()) {
            return;
        }
        String dir = uploadDir + java.io.File.separator + pgManagementEntity.getId() + java.io.File.separator;
        fileStorageService.uploadFile(newPhotos, dir);
    }
}
