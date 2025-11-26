package com.easy.stazy.pgmanagement.admin.service.handler;

import com.easy.stazy.pgmanagement.admin.dto.request.PgManagementRequestDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgDetailsResponseDto;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.entities.AmenityEntity;
import com.easy.stazy.pgmanagement.pg.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PgAmenitiesHandler implements PgFieldHandler {

    private final AmenityRepository amenityRepository;

    @Override
    public void update(PgManagementRequestDto dto, PgManagementEntity entity) {
        if (dto.getAmenitys() != null) {
            List<String> incomingAmenityNames = dto.getAmenitys();
            List<AmenityEntity> currentAmenities = entity.getAmenities() != null ? entity.getAmenities() : new ArrayList<>();
            List<AmenityEntity> updatedAmenities = new ArrayList<>();

            // Remove amenities not in the incoming list
            currentAmenities.removeIf(a -> !incomingAmenityNames.contains(a.getName()));

            // Add or keep amenities
            for (String name : incomingAmenityNames) {
                AmenityEntity amenity = currentAmenities.stream()
                        .filter(a -> a.getName().equals(name))
                        .findFirst()
                        .orElse(null);
                if (amenity != null) {
                    updatedAmenities.add(amenity);
                } else {
                    AmenityEntity newAmenity = new AmenityEntity();
                    newAmenity.setName(name.trim());
                    newAmenity.setPg(entity); // Set parent PG
                    updatedAmenities.add(amenityRepository.save(newAmenity));
                }
            }
            currentAmenities.clear();
            currentAmenities.addAll(updatedAmenities);
            entity.setAmenities(currentAmenities);
        }
    }

    // For PG creation, use the same logic as update to ensure consistency
    public void create(PgManagementRequestDto dto, PgManagementEntity entity) {
        update(dto, entity);
    }

    @Override
    public void handle(PgManagementEntity entity, PgDetailsResponseDto dto) {
        if (entity.getAmenities() != null) {
            dto.setAmenitys(entity.getAmenities().stream().map(a -> a.getName()).toList());
        }
    }
}
