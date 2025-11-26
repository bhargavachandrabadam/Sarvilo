package com.easy.stazy.photos;

import java.util.List;

public interface PhotoPathUtilsService {
    List<String> toRelativePhotoPaths(List<String> photoPaths);

    List<String> toFullPhotoUrls(List<String> photoPaths);
}

