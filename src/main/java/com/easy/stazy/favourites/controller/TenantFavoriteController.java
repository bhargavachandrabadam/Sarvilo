package com.easy.stazy.favourites.controller;

import com.easy.stazy.pgmanagement.pg.dto.response.PgFilterResponseDto;
import com.easy.stazy.favourites.service.FavoriteService;
import com.easy.stazy.shared.common.util.SuccessResponseBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tenant Favorite Controller", description = "APIs for Tenants to manage their favorite PGs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tenant/favorites")
public class TenantFavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestParam Long userId, @RequestParam Long pgId) {
        favoriteService.addFavorite(userId, pgId);
        return ResponseEntity.ok("Favorite added successfully");
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long pgId) {
        favoriteService.removeFavorite(userId, pgId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<SuccessResponseBody> getFavorites(@RequestParam Long userId) {
        List<PgFilterResponseDto> favorites = favoriteService.getFavorites(userId);
        SuccessResponseBody responseBody = new SuccessResponseBody("Favorites retrieved successfully", favorites);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}