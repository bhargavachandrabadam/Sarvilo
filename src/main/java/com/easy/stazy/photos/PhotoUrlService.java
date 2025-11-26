package com.easy.stazy.photos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PhotoUrlService {
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public String getPhotoUrl(String photoPath) {
        if (photoPath == null || photoPath.isEmpty()) return null;
        // Remove any leading slashes for consistency
        String cleanPath = photoPath.replaceAll("^/+", "");
        return baseUrl + "/images/" + cleanPath.replace("\\", "/");
    }
}

