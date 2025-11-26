package com.easy.stazy.reviews.controller;

import com.easy.stazy.reviews.service.ReviewService;
import com.easy.stazy.shared.common.util.SuccessResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/owner/reviews")
@Tag(name = "Owner Reviews", description = "Owner-side review access")
@RequiredArgsConstructor
public class OwnerReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Get all reviews for a specific PG")
    @GetMapping("/{pgId}")
    public ResponseEntity<SuccessResponseBody> getReviewsByPgId(@PathVariable Long pgId) {
        return ResponseEntity.ok(new SuccessResponseBody("Reviews fetched successfully",
                reviewService.getReviewsByPgId(pgId)));
    }
}
