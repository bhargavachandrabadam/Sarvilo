package com.easy.stazy.pgmanagement.admin.service.handler;

import com.easy.stazy.pgmanagement.admin.dto.request.PgManagementRequestDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgDetailsResponseDto;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.entities.RentalOptionEntity;
import com.easy.stazy.pgmanagement.pg.dto.request.RentalOptionRequestDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
public class PgRentalOptionsHandler implements PgFieldHandler {
    @Override
    public void update(PgManagementRequestDto dto, PgManagementEntity entity) {
        if (dto.getRentalOptions() != null) {
            // Prepare unique incoming options (by rentalType and sharingType)
            Map<String, RentalOptionRequestDto> incomingOptionsMap = new HashMap<>();
            for (RentalOptionRequestDto o : dto.getRentalOptions()) {
                String key = o.getRentalType() + "-" + o.getSharingType();
                incomingOptionsMap.putIfAbsent(key, o);
            }

            // Prepare existing options map (by rentalType and sharingType)
            Map<String, RentalOptionEntity> existingOptionsMap = new HashMap<>();
            if (entity.getRentalOptions() != null) {
                for (RentalOptionEntity o : entity.getRentalOptions()) {
                    String key = o.getDurationType() + "-" + o.getSharingType();
                    existingOptionsMap.putIfAbsent(key, o);
                }
            }

            // Find options to keep (present in both DB and DTO)
            List<RentalOptionEntity> toKeep = new ArrayList<>();
            for (String key : existingOptionsMap.keySet()) {
                if (incomingOptionsMap.containsKey(key)) {
                    toKeep.add(existingOptionsMap.get(key));
                }
            }

            // Find new options to insert (present in DTO but not in DB)
            List<RentalOptionEntity> toInsert = new ArrayList<>();
            for (String key : incomingOptionsMap.keySet()) {
                if (!existingOptionsMap.containsKey(key)) {
                    RentalOptionRequestDto optionDto = incomingOptionsMap.get(key);
                    RentalOptionEntity option = new RentalOptionEntity();
                    option.setDurationType(optionDto.getRentalType());
                    option.setSharingType(optionDto.getSharingType());
                    option.setPrice(optionDto.getRent());
                    option.setCautionDeposit(optionDto.getCautionDeposit() != null ? optionDto.getCautionDeposit() : BigDecimal.ZERO);
                    option.setActive(true);
                    option.setPg(entity);
                    toInsert.add(option);
                }
            }

            // Find options to delete (present in DB but not in DTO)
            List<RentalOptionEntity> toDelete = new ArrayList<>();
            for (String key : existingOptionsMap.keySet()) {
                if (!incomingOptionsMap.containsKey(key)) {
                    toDelete.add(existingOptionsMap.get(key));
                }
            }
            // Remove deleted options from entity
            if (!toDelete.isEmpty() && entity.getRentalOptions() != null) {
                entity.getRentalOptions().removeAll(toDelete);
            }

            // Ensure rentalOptions list is initialized
            if (entity.getRentalOptions() == null) {
                entity.setRentalOptions(new ArrayList<>());
            }
            // Update the entity's rentalOptions collection using clear() and addAll() for proper orphan removal
            entity.getRentalOptions().clear();
            entity.getRentalOptions().addAll(toKeep);
            entity.getRentalOptions().addAll(toInsert);
        }
    }

    @Override
    public void handle(PgManagementEntity entity, PgDetailsResponseDto dto) {
        if (entity.getRentalOptions() != null) {
            List<PgDetailsResponseDto.RentalOptionDto> rentalOptionDtos = new ArrayList<>();
            for (RentalOptionEntity opt : entity.getRentalOptions()) {
                PgDetailsResponseDto.RentalOptionDto ro = new PgDetailsResponseDto.RentalOptionDto();
                ro.setId(opt.getId());
                ro.setRentalType(opt.getDurationType() != null ? opt.getDurationType().toString() : null);
                ro.setSharingType(opt.getSharingType());
                ro.setRent(opt.getPrice() != null ? opt.getPrice().doubleValue() : 0.0);
                ro.setCautionDeposit(opt.getCautionDeposit() != null ? opt.getCautionDeposit().doubleValue() : 0.0);
                rentalOptionDtos.add(ro);
            }
            dto.setRentalOptions(rentalOptionDtos);
        }
    }
}
