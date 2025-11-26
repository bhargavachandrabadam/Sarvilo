package com.easy.stazy.pgmanagement.pg.validations.impl;

import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.pgmanagement.pg.dto.request.FloorRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.FloorEntity;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.repository.FloorRepository;
import com.easy.stazy.pgmanagement.pg.validations.FloorValidator;
import com.easy.stazy.shared.common.exception.AlreadyExistsException;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FloorValidatorImpl implements FloorValidator {
    private final FloorRepository floorRepository;
    private final PgManagementRepository pgRepository;

    @Override
    public PgManagementEntity validate(long pgId, FloorRequestDto dto) {
        PgManagementEntity pg = pgRepository.findById(pgId)
                .orElseThrow(() -> new ResourceNotFoundException("PG not found"));
        FloorEntity existing = floorRepository.findByFloorNumberAndPg(dto.getFloorNumber(), pg);
        if (existing != null) {
            throw new AlreadyExistsException("A floor with this name already exists in the selected PG");
        }
        return pg;
    }
}
