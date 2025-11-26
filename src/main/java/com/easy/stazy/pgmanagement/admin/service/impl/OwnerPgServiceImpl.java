package com.easy.stazy.pgmanagement.admin.service.impl;

import com.easy.stazy.pgmanagement.pg.dto.response.OwnerPgResponseDto;
import com.easy.stazy.pgmanagement.admin.service.OwnerPgService;
import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.pgmanagement.pg.entities.FloorEntity;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.repository.BedOccupancyRepository;
import com.easy.stazy.pgmanagement.pg.repository.BedRepository;
import com.easy.stazy.reviews.repository.ReviewRepository;
import com.easy.stazy.photos.PhotoPathUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerPgServiceImpl implements OwnerPgService {

    private final PgManagementRepository pgRepository;
    private final BedRepository bedRepository;
    private final BedOccupancyRepository bedOccupancyRepository;
    private final ReviewRepository reviewRepository;
    private final PhotoPathUtilsService photoPathUtilsService;

    @Override
    public List<OwnerPgResponseDto> getPgsByOwner(Long ownerId) {
        List<PgManagementEntity> pgs = pgRepository.findByOwner_Id(ownerId);
        return pgs.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private OwnerPgResponseDto mapToDto(PgManagementEntity pg) {
        OwnerPgResponseDto dto = new OwnerPgResponseDto();
        dto.setPgId(pg.getId());
        dto.setPgName(pg.getPgName());
        dto.setDescription(pg.getDescription());
        dto.setManagerName(pg.getManagerName());
        dto.setBookingStatus(pg.getStatus());
        long totalBeds = bedRepository.countByPgId(pg.getId());
        long occupied = bedOccupancyRepository.countActiveByPgId(pg.getId());
        dto.setTotalBeds(totalBeds);
        dto.setOccupiedBeds(occupied);
        long ratingCount = reviewRepository.countByPgId(pg.getId());
        Double avg = reviewRepository.avgRatingByPgId(pg.getId());
        dto.setRatingCount(ratingCount);
        dto.setAverageRating(avg == null ? 0.0 : avg);
        List<String> floors = pg.getFloors().stream()
                .map(FloorEntity::getFloorNumber)
                .collect(Collectors.toList());
        List<Object[]> yearMonths = bedOccupancyRepository.findDistinctYearMonthsByPgId(pg.getId());
        List<YearMonth> yearMonthsList = yearMonths.stream()
                .map(arr -> YearMonth.of(((Number) arr[0]).intValue(), ((Number) arr[1]).intValue()))
                .toList();
        dto.setFloors(floors);
        dto.setYearMonths(yearMonthsList);
        OwnerPgResponseDto.LocationDto loc = new OwnerPgResponseDto.LocationDto();
        loc.setLocation(pg.getLocation());
        loc.setDistrict(pg.getDistrict());
        loc.setState(pg.getState());
        loc.setPinCode(pg.getPinCode());
        dto.setLocation(loc);
        if (pg.getPhotoPaths() != null) {
            dto.setPhotos(photoPathUtilsService.toFullPhotoUrls(pg.getPhotoPaths()));
        } else {
            dto.setPhotos(List.of());
        }
        return dto;
    }
}

