package com.easy.stazy.reviews.validations;

import com.easy.stazy.reviews.dto.request.ReviewRequestDto;

public interface ReviewValidator {
    void validate(long pgId, long tenantId, ReviewRequestDto dto);
}

