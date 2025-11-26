package com.easy.stazy.pgmanagement.pg.mapper.responses;

import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.dto.response.HostelDetailsResponseDto;
import org.springframework.stereotype.Component;

@Component
public class HostelDetailsResponseMapper {

    public HostelDetailsResponseDto hostelDetailsResponseMapper(PgManagementEntity hostelEntity) {
        if(hostelEntity == null) {
            return null;
        }
        return HostelDetailsResponseDto.builder()
                .pgName(hostelEntity.getPgName())
                .ownerName(hostelEntity.getOwner() != null ? hostelEntity.getOwner().getOwnerName() : null)
                .mobileNumber(hostelEntity.getMobileNumber())
                .pgManagerName(hostelEntity.getManagerName())
                .pgManagerContactNumber(hostelEntity.getManagerContactNumber())
                .pgManagerAdditionalContactNumber(hostelEntity.getAdditionalContactNumber())
                .state(hostelEntity.getState())
                .city(hostelEntity.getDistrict())
                .pincode(hostelEntity.getPinCode())
                .description(hostelEntity.getDescription())
                .locality(hostelEntity.getLocation())
                .build();
    }
}
