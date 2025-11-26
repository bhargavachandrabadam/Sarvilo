package com.easy.stazy.photos;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocalPhotoPathUtilsService implements PhotoPathUtilsService {
    private static final String UPLOADS_ROOT = "uploads";

    @Override
    public List<String> toRelativePhotoPaths(List<String> photoPaths) {
        if (photoPaths == null) return new ArrayList<>();
        List<String> relativePaths = new ArrayList<>();
        for (String path : photoPaths) {
            if (path == null) continue;
            String normalized = path.replace("\\", "/");
            int idx = normalized.indexOf(UPLOADS_ROOT + "/");
            if (idx != -1) {
                relativePaths.add(normalized.substring(idx + UPLOADS_ROOT.length() + 1));
            } else {
                int lastIdx = normalized.lastIndexOf(UPLOADS_ROOT + "/");
                if (lastIdx != -1) {
                    relativePaths.add(normalized.substring(lastIdx + UPLOADS_ROOT.length() + 1));
                } else {
                    relativePaths.add(normalized);
                }
            }
        }
        return relativePaths;
    }

    @Override
    public List<String> toFullPhotoUrls(List<String> photoPaths) {
        if (photoPaths == null) return new ArrayList<>();
        List<String> urls = new ArrayList<>();
        for (String path : toRelativePhotoPaths(photoPaths)) {
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/")
                    .path(path)
                    .toUriString();
            urls.add(url);
        }
        return urls;
    }
}

