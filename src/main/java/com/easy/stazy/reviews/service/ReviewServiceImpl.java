package com.easy.stazy.reviews.service;


import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.reviews.dto.request.ReviewRequestDto;
import com.easy.stazy.reviews.dto.response.ReviewResponseDto;
import com.easy.stazy.reviews.entity.ReviewEntity;
import com.easy.stazy.reviews.repository.ReviewRepository;
import com.easy.stazy.reviews.validations.ReviewValidator;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.pgmanagement.tenant.repository.TenantInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ReviewService interface for managing reviews in the hostel module.
 * <p>
 * This service provides business logic for creating and retrieving reviews associated with PG (Paying Guest) accommodations.
 * It interacts with repositories for tenants, PGs, and reviews to persist and retrieve data, and uses ReviewValidator to enforce
 * business rules such as preventing duplicate reviews by the same tenant for the same PG. All data-modifying operations are transactional
 * to ensure data consistency.
 * <p>
 * Typical use cases include:
 * <ul>
 *   <li>Creating a new review for a PG by a tenant</li>
 *   <li>Retrieving all reviews for a specific PG</li>
 * </ul>
 *
 */
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final TenantInfoRepository tenantInfoRepository;

    private final PgManagementRepository pgRepository;

    private final ReviewRepository reviewRepository;

    private final ReviewValidator reviewValidator;

    /**
     * Creates a new review for a given PG (Paying Guest) by a specific tenant.
     * <p>
     * This method first validates the review using the ReviewValidator to ensure that the tenant has not already submitted a review
     * for the same PG and that all business rules are satisfied. It then maps the ReviewRequestDto to a ReviewEntity, sets the tenant
     * and PG references, and persists the new review in the database. If validation fails or the tenant/PG does not exist, an exception is thrown.
     * This operation is transactional to ensure data consistency.
     *
     * @param pgId     the ID of the PG to which the review should be assigned
     * @param tenantId the ID of the tenant submitting the review
     * @param dto      the DTO containing review details (title, description, rating, etc.)
     * @throws IllegalArgumentException if validation fails or the tenant/PG does not exist
     */
    @Transactional
    public void createReview(long pgId, long tenantId, ReviewRequestDto dto) {
        reviewValidator.validate(pgId, tenantId, dto);
        ReviewEntity review = new ReviewEntity();
        review.setTitle(dto.getTitle());
        review.setRating(dto.getRating());
        TenantInfoEntity tenantInfoEntity = tenantInfoRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        review.setTenant(tenantInfoEntity);
        review.setDescription(dto.getDescription());
        PgManagementEntity pg = pgRepository.findById(pgId).orElseThrow(() -> new ResourceNotFoundException("PG not found"));
        review.setPg(pg);
        reviewRepository.save(review);
    }

    /**
     * Retrieves all reviews for a given PG (Paying Guest) by its ID.
     * <p>
     * This method fetches all ReviewEntity records associated with the specified PG ID using the reviewRepository.
     * For each review, it constructs a ReviewResponseDto, populating fields such as tenant name, title, description, rating, and updated date.
     * The resulting list of DTOs is returned for use in the API response or UI. This operation is transactional and read-only for performance and consistency.
     *
     * @param pgId the unique identifier of the PG for which reviews are to be retrieved
     * @return a list of ReviewResponseDto objects representing all reviews for the specified PG
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByPgId(Long pgId) {
        return reviewRepository.findByPgId(pgId).stream()
                .map(review -> {
                    ReviewResponseDto dto = new ReviewResponseDto();
                    dto.setTenantName(review.getTenant().getUser().getName());
                    dto.setTitle(review.getTitle());
                    dto.setDescription(review.getDescription());
                    dto.setRating(review.getRating());
                    dto.setUpdatedAt(review.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
