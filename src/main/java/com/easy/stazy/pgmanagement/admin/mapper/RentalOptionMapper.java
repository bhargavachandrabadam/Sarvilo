package com.easy.stazy.pgmanagement.admin.mapper;

import com.easy.stazy.pgmanagement.pg.dto.response.PgFilterResponseDto;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RentalOptionMapper {
    public List<PgFilterResponseDto.RentalOptionDto> filterRentalOptions(PgManagementEntity pg, Double minPrice, Double maxPrice, List<String> sharingType) {
        if (pg.getRentalOptions() == null) return new ArrayList<>();
        return pg.getRentalOptions().stream()
                .filter(opt ->
                        (minPrice == null || opt.getPrice().doubleValue() >= minPrice) &&
                                (maxPrice == null || opt.getPrice().doubleValue() <= maxPrice) &&
                                (sharingType.isEmpty() || (opt.getSharingType() != null && sharingType.contains(opt.getSharingType())))
                ).map(opt -> {
                    PgFilterResponseDto.RentalOptionDto ro = new PgFilterResponseDto.RentalOptionDto();
                    ro.setRentalType(opt.getDurationType().name());
                    ro.setSharingType(opt.getSharingType() != null ? List.of(opt.getSharingType()) : new ArrayList<>());
                    ro.setPrice(opt.getPrice().doubleValue());
                    ro.setCautionDeposit(opt.getCautionDeposit() != null ? opt.getCautionDeposit().doubleValue() : null);
                    return ro;
                })
                .toList();
    }

    public List<PgFilterResponseDto.RentalOptionDto> mapRentalOptions(PgManagementEntity pg) {
        if (pg.getRentalOptions() == null) return new ArrayList<>();
        return pg.getRentalOptions().stream()
                .map(opt -> {
                    PgFilterResponseDto.RentalOptionDto ro = new PgFilterResponseDto.RentalOptionDto();
                    ro.setRentalType(opt.getDurationType().name());
                    ro.setSharingType(opt.getSharingType() != null ? List.of(opt.getSharingType()) : new ArrayList<>());
                    ro.setPrice(opt.getPrice().doubleValue());
                    ro.setCautionDeposit(opt.getCautionDeposit() != null ? opt.getCautionDeposit().doubleValue() : null);
                    return ro;
                })
                .toList();
    }
}

