package com.easy.stazy.reviews.service;


import com.easy.stazy.reviews.dto.request.ReviewRequestDto;
import com.easy.stazy.reviews.dto.response.ReviewResponseDto;

import java.util.List;

public interface ReviewService {

     void createReview(long pgId, long tenantId, ReviewRequestDto dto);

     List<ReviewResponseDto> getReviewsByPgId(Long pgId);
}
