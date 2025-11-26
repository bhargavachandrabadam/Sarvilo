package com.easy.stazy.pgmanagement.pg.mapper.requests;

import com.easy.stazy.pgmanagement.pg.dto.request.FloorRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.FloorEntity;
import org.springframework.stereotype.Component;

@Component
public class FloorRequestMapper {
    public FloorEntity toEntity(FloorRequestDto request) {
        if(request == null) {
            return null;
        }
        FloorEntity entity = new FloorEntity();
        entity.setFloorNumber(request.getFloorNumber());
        return entity;
    }
}

