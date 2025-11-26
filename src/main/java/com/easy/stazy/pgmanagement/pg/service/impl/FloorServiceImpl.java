package com.easy.stazy.pgmanagement.pg.service.impl;

import com.easy.stazy.pgmanagement.pg.dto.request.FloorRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.FloorEntity;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.mapper.requests.FloorRequestMapper;
import com.easy.stazy.pgmanagement.pg.repository.FloorRepository;
import com.easy.stazy.pgmanagement.pg.service.FloorService;
import com.easy.stazy.pgmanagement.pg.validations.FloorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the FloorService interface for managing floor operations in the hostel module.
 * <p>
 * This service provides business logic for creating floors associated with a specific PG (Paying Guest) entity.
 * It interacts with the FloorRepository for persistence, uses FloorRequestMapper for converting DTOs to entities,
 * and FloorValidator for validating business rules and PG existence. All data-modifying operations are transactional
 * to ensure data consistency.
 * <p>
 * Typical use cases include:
 * <ul>
 *   <li>Creating a new floor for a specific PG</li>
 *   <li>Validating floor uniqueness and business rules before creation</li>
 * </ul>
 *
 */
@RequiredArgsConstructor
@Service
public class FloorServiceImpl implements FloorService {
    private final FloorRepository floorRepository;
    private final FloorRequestMapper floorRequestMapper;
    private final FloorValidator floorValidator;

    /**
     * Creates a new floor for a given PG (Paying Guest) entity.
     * <p>
     * This method first validates the PG and floor request using the FloorValidator, ensuring that the floor name
     * is unique within the PG and that all business rules are satisfied. It then maps the FloorRequestDto to a
     * FloorEntity, sets the PG reference, and persists the new floor in the database. If validation fails, an
     * exception is thrown. This operation is transactional to ensure data consistency.
     *
     * @param request the DTO containing floor details (such as floor number, type, etc.)
     * @param pgId    the ID of the PG to which the floor should be assigned
     * @throws IllegalArgumentException if validation fails or the PG does not exist
     */
    @Transactional
    @Override
    public void createFloor(FloorRequestDto request, long pgId) {
        PgManagementEntity pg = floorValidator.validate(pgId, request);
        FloorEntity entity = floorRequestMapper.toEntity(request);
        entity.setPg(pg);
        floorRepository.save(entity);
    }
}
