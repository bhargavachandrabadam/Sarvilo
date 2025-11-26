package com.easy.stazy.photos;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.io.File;

public interface FileStorageService {
    List<String> uploadFile(List<MultipartFile> files, String uploadDir);
    boolean deleteFile(String filePath);
    String moveFileToTenantFolder(String oldUrl, Long tenantId, String baseUploadDir, String subFolder);
    List<String> uploadFileFromFile(File file, String uploadDir);
}