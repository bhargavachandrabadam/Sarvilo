package com.easy.stazy.pgmanagement.pg.mapper.requests;

import com.easy.stazy.pgmanagement.pg.dto.request.BedRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.BedEntity;
import org.springframework.stereotype.Component;

@Component
public class BedRequestMapper {
    public BedEntity toEntity(BedRequestDto request) {
        if(request == null) {
            return null;
        }
        BedEntity entity = new BedEntity();
        entity.setBedNumber(request.getBedNumber());
        entity.setDescription(request.getDescription());
        return entity;
    }
}

