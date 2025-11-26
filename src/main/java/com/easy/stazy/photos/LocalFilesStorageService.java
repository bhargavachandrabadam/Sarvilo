package com.easy.stazy.photos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Local implementation of the FileStorageService for handling file uploads and deletions
 * on the local file system.
 * <p>
 * This service provides methods to upload files to a specified directory and delete files
 * from the local storage. It is primarily used for storing and managing photo files
 * associated with PG entities and other resources.
 * </p>
 */
@Slf4j
@Service
public class LocalFilesStorageService implements FileStorageService {
    /**
     * Uploads a list of files to the specified directory on the local file system.
     *
     * @param files     List of MultipartFile objects to be uploaded.
     * @param uploadDir The directory path where files should be uploaded.
     * @return List of absolute file paths where the files have been uploaded.
     * @throws RuntimeException if the upload directory cannot be created.
     */
    @Override
    public List<String> uploadFile(List<MultipartFile> files, String uploadDir) {
        List<String> photoPaths = new ArrayList<>();
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create upload directory: " + uploadDir);
            }
        }
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                File dest = new File(dir, originalFilename);
                try {
                    file.transferTo(dest);
                    photoPaths.add(dest.getAbsolutePath());
                } catch (IOException e) {
                    log.error("Failed to transfer file: {}", originalFilename, e);
                }
            }
        }
        return photoPaths;
    }

    @Override
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    /**
     * Moves a file from the old location to a tenant folder and returns the new URL.
     * @param oldUrl The original file path
     * @param tenantId The tenant's ID
     * @param baseUploadDir The base directory for tenant files
     * @param subFolder The subfolder (e.g., "id-proof-photos" or "profile-photos")
     * @return The new file path after moving, or the old path if move fails
     */
    @Override
    public String moveFileToTenantFolder(String oldUrl, Long tenantId, String baseUploadDir, String subFolder) {
        if (oldUrl == null) return null;
        String baseDir = baseUploadDir + java.io.File.separator + tenantId + java.io.File.separator + subFolder + java.io.File.separator;
        File dir = new File(baseDir);
        if (!dir.exists()) dir.mkdirs();
        File oldFile = new File(oldUrl.replace("file:/", ""));
        File newFile = new File(dir, oldFile.getName());
        try {
            java.nio.file.Files.move(oldFile.toPath(), newFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return newFile.getAbsolutePath();
        } catch (Exception e) {
            log.error("Failed to move file from {} to {}", oldFile.getAbsolutePath(), newFile.getAbsolutePath(), e);
            return oldUrl;
        }
    }

    /**
     * Uploads a single file from the local file system to the specified upload directory.
     *
     * @param file      The File object to be uploaded.
     * @param uploadDir The directory path where the file should be uploaded.
     * @return List containing the absolute file path where the file has been uploaded.
     * @throws RuntimeException if the upload directory cannot be created.
     */
    @Override
    public List<String> uploadFileFromFile(File file, String uploadDir) {
        List<String> photoPaths = new ArrayList<>();
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create upload directory: " + uploadDir);
            }
        }
        if (file == null || !file.exists()) {
            log.error("Source file does not exist: {}", file != null ? file.getAbsolutePath() : "null");
            return photoPaths;
        }
        String fileName = file.getName();
        File dest = new File(dir, fileName);
        // If file exists, append timestamp to filename
        if (dest.exists()) {
            String baseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
            String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
            String uniqueName = baseName + "_" + System.currentTimeMillis() + ext;
            dest = new File(dir, uniqueName);
        }
        try {
            java.nio.file.Files.copy(file.toPath(), dest.toPath());
            photoPaths.add(dest.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to copy file: {}", file.getName(), e);
        }
        return photoPaths;
    }
}
