package com.easy.stazy.pgmanagement.pg.validations;


import com.easy.stazy.pgmanagement.pg.dto.request.FloorRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;

public interface FloorValidator {
    PgManagementEntity validate(long pgId, FloorRequestDto dto);
}
