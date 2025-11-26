package com.easy.stazy.reviews.controller;


import com.easy.stazy.reviews.dto.request.ReviewRequestDto;
import com.easy.stazy.reviews.service.ReviewService;
import com.easy.stazy.shared.common.util.SuccessResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tenant Reviews", description = "APIs for Tenants to create reviews for PGs")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/tenant/reviews")
public class TenantReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Create a review for a PG by a tenant")
    @PostMapping("/{pgId}/{tenantId}")
    public ResponseEntity<SuccessResponseBody> createReview(
            @PathVariable long pgId,
            @PathVariable long tenantId,
            @Valid @RequestBody ReviewRequestDto dto) {

        reviewService.createReview(pgId, tenantId, dto);
        return new ResponseEntity<>(new SuccessResponseBody("Review created successfully", null), HttpStatus.CREATED);
    }
}

