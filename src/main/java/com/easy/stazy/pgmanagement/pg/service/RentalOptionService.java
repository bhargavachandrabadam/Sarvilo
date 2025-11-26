package com.easy.stazy.pgmanagement.pg.service;

import com.easy.stazy.pgmanagement.pg.dto.request.RentalOptionRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.RentalOptionEntity;

public interface RentalOptionService {
    RentalOptionEntity createRentalOption(Long pgId, RentalOptionRequestDto dto);
    RentalOptionEntity updateRentalOption(Long id, RentalOptionRequestDto dto);
}

