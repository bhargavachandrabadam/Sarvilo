package com.easy.stazy.shared.common.dto;

import lombok.Builder;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;

@Builder(toBuilder = true)
public record MailParams(@NonNull String to,
                         @NonNull String subject,
                         @NonNull String body,
                         @NonNull Boolean isHtml,
                         List<String> attachment) {
    @Override
    public List<String> attachment() {
        return attachment == null ? Collections.emptyList() : attachment;
    }
}
