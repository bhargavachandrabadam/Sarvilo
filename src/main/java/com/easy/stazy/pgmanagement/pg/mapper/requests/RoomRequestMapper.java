package com.easy.stazy.pgmanagement.pg.mapper.requests;

import com.easy.stazy.pgmanagement.pg.dto.request.RoomRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.RoomEntity;
import org.springframework.stereotype.Component;

@Component
public class RoomRequestMapper {
    public RoomEntity toEntity(RoomRequestDto request) {
        if(request == null) {
            return null;
        }
        RoomEntity entity = new RoomEntity();
        entity.setRoomNumber(request.getRoomNumber());
        entity.setDescription(request.getDescription());
        return entity;
    }
}

