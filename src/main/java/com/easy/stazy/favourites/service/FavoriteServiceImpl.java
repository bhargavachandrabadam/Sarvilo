package com.easy.stazy.favourites.service;

import com.easy.stazy.pgmanagement.pg.dto.response.PgFilterResponseDto;
import com.easy.stazy.favourites.entity.FavoriteEntity;
import com.easy.stazy.pgmanagement.admin.mapper.PgResponseMapper;
import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.favourites.repository.FavoriteRepository;
import com.easy.stazy.authentication.repository.UserRepository;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final PgManagementRepository pgRepository;
    private final PgResponseMapper pgResponseMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long pgId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User does not exist: " + userId);
        }
        if (!pgRepository.existsById(pgId)) {
            throw new ResourceNotFoundException("PG does not exist: " + pgId);
        }
        Optional<FavoriteEntity> existing = favoriteRepository.findByUserIdAndPgId(userId, pgId);
        if (existing.isPresent()) {
            return;
        }
        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setUserId(userId);
        favorite.setPgId(pgId);
        favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long pgId) {
        favoriteRepository.deleteByUserIdAndPgId(userId, pgId);
    }

    @Override
    public List<PgFilterResponseDto> getFavorites(Long userId) {
        List<FavoriteEntity> favorites = favoriteRepository.findByUserId(userId);
        List<PgFilterResponseDto> responseList = new ArrayList<>();
        for (FavoriteEntity favorite : favorites) {
            pgRepository.findById(favorite.getPgId()).ifPresent(pg -> responseList.add(pgResponseMapper.mapToDto(pg)));
        }
        return responseList;
    }
}

