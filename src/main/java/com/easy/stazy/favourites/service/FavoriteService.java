package com.easy.stazy.favourites.service;



import com.easy.stazy.pgmanagement.pg.dto.response.PgFilterResponseDto;

import java.util.List;

public interface FavoriteService {
    void addFavorite(Long userId, Long pgId);
    void removeFavorite(Long userId, Long pgId);
    List<PgFilterResponseDto> getFavorites(Long userId);
}


