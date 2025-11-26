package com.easy.stazy.pgmanagement.tenant.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FileRequestDto {
    private MultipartFile file;
    private List<MultipartFile> photos;
    private List<MultipartFile> profilePhotos;
}
