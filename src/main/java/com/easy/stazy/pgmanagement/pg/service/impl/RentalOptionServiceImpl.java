package com.easy.stazy.pgmanagement.pg.service.impl;

import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.pgmanagement.pg.dto.request.RentalOptionRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.entities.RentalOptionEntity;
import com.easy.stazy.pgmanagement.pg.repository.RentalOptionRepository;
import com.easy.stazy.pgmanagement.pg.service.RentalOptionService;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalOptionServiceImpl implements RentalOptionService {

    private final RentalOptionRepository rentalOptionRepository;
    private final PgManagementRepository pgRepository;

    @Override
    @Transactional
    public RentalOptionEntity createRentalOption(Long pgId, RentalOptionRequestDto dto) {
        PgManagementEntity pg = pgRepository.findById(pgId)
                .orElseThrow(() -> new ResourceNotFoundException("PG not found with id: " + pgId));
        RentalOptionEntity entity = new RentalOptionEntity();
        entity.setDurationType(dto.getRentalType());
        entity.setSharingType(dto.getSharingType());
        entity.setPrice(dto.getRent());
        entity.setCautionDeposit(dto.getCautionDeposit());
        entity.setActive(true);
        entity.setPg(pg);
        return rentalOptionRepository.save(entity);
    }

    @Override
    @Transactional
    public RentalOptionEntity updateRentalOption(Long id, RentalOptionRequestDto dto) {
        RentalOptionEntity entity = rentalOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental option not found with id: " + id));
        entity.setDurationType(dto.getRentalType());
        entity.setSharingType(dto.getSharingType());
        entity.setPrice(dto.getRent());
        entity.setCautionDeposit(dto.getCautionDeposit());
        return rentalOptionRepository.save(entity);
    }
}

