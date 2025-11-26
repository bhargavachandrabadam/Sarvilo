package com.easy.stazy.pgmanagement.admin.mapper;

import com.easy.stazy.pgmanagement.pg.dto.response.PgFilterResponseDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgUserListResponseDto;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.reviews.entity.ReviewEntity;
import com.easy.stazy.reviews.repository.ReviewRepository;
import com.easy.stazy.photos.PhotoPathUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PgResponseMapper {

    private final ReviewRepository reviewRepository;
    private final RentalOptionMapper rentalOptionMapper;
    private final PhotoPathUtilsService photoPathUtilsService;

    public PgFilterResponseDto mapToDto(PgManagementEntity pg, Double minPrice, Double maxPrice, List<String> sharingType) {
        PgFilterResponseDto dto = new PgFilterResponseDto();
        dto.setPgId(pg.getId());
        dto.setPgName(pg.getPgName());
        dto.setPhotos(photoPathUtilsService.toFullPhotoUrls(pg.getPhotoPaths() != null ? pg.getPhotoPaths() : new ArrayList<>()));
        dto.setAddress((pg.getLocation() != null ? pg.getLocation() + ", " : "") + (pg.getState() != null ? pg.getState() + ", " : "") + (pg.getDistrict() != null ? pg.getDistrict() + ", " : "") + (pg.getPinCode() != null ? pg.getPinCode() : ""));
        List<ReviewEntity> reviews = reviewRepository.findByPgId(pg.getId());
        dto.setReviewsCount(reviews.size());
        double avgRating = reviews.stream().mapToDouble(r -> r.getRating() != null ? r.getRating() : 0.0).average().orElse(0.0);
        dto.setAverageRating(avgRating);
        dto.setRentalOptions(rentalOptionMapper.filterRentalOptions(pg, minPrice, maxPrice, sharingType));
        dto.setStatus(pg.getStatus());
        return dto;
    }

    public PgFilterResponseDto mapToDto(PgManagementEntity pg) {
        PgFilterResponseDto dto = new PgFilterResponseDto();
        dto.setPgId(pg.getId());
        dto.setPgName(pg.getPgName());
        dto.setPhotos(photoPathUtilsService.toFullPhotoUrls(pg.getPhotoPaths()));
        dto.setAddress(pg.getLocation() + " " + pg.getState() + " " + pg.getDistrict() + " " + pg.getPinCode());
        List<ReviewEntity> reviews = reviewRepository.findByPgId(pg.getId());
        dto.setReviewsCount(reviews.size());
        double avgRating = reviews.stream().mapToDouble(r -> r.getRating() != null ? r.getRating() : 0.0).average().orElse(0.0);
        dto.setAverageRating(avgRating);
        dto.setRentalOptions(rentalOptionMapper.mapRentalOptions(pg));
        dto.setStatus(pg.getStatus());
        return dto;
    }

    public PgUserListResponseDto mapToUserListDto(PgManagementEntity pg) {
        PgUserListResponseDto dto = new PgUserListResponseDto();
        dto.setPgId(pg.getId());
        dto.setPgName(pg.getPgName());
        dto.setLocation(pg.getLocation());
        dto.setState(pg.getState());
        dto.setDistrict(pg.getDistrict());
        dto.setPincode(pg.getPinCode());
        List<ReviewEntity> reviews = reviewRepository.findByPgId(pg.getId());
        dto.setReviewsCount(reviews.size());
        double avgRating = reviews.stream().mapToDouble(r -> r.getRating() != null ? r.getRating() : 0.0).average().orElse(0.0);
        dto.setAverageRating(avgRating);
        dto.setManagerName(pg.getManagerName());
        dto.setRentalOptionId(pg.getRentalOptions() != null && !pg.getRentalOptions().isEmpty() ? pg.getRentalOptions().get(0).getId() : null);
        return dto;
    }


}
