package com.easy.stazy.reviews.validations;

import com.easy.stazy.reviews.dto.request.ReviewRequestDto;
import com.easy.stazy.reviews.repository.ReviewRepository;
import com.easy.stazy.shared.common.exception.AlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewValidatorImpl implements ReviewValidator {
    private final ReviewRepository reviewRepository;

    @Override
    public void validate(long pgId, long tenantId, ReviewRequestDto dto) {
        boolean exists = reviewRepository.existsByPgIdAndTenantId(pgId, tenantId);
        if (exists) {
            throw new AlreadyExistsException("Tenant has already reviewed this PG");
        }
    }
}

