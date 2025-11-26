package com.easy.stazy.pgmanagement.pg.service.impl;

import com.easy.stazy.pgmanagement.pg.dto.request.RoomRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.FloorEntity;
import com.easy.stazy.pgmanagement.pg.entities.RentalOptionEntity;
import com.easy.stazy.pgmanagement.pg.entities.RoomEntity;
import com.easy.stazy.pgmanagement.pg.mapper.requests.RoomRequestMapper;
import com.easy.stazy.pgmanagement.pg.repository.FloorRepository;
import com.easy.stazy.pgmanagement.pg.repository.RentalOptionRepository;
import com.easy.stazy.pgmanagement.pg.repository.RoomRepository;
import com.easy.stazy.pgmanagement.pg.service.RoomService;
import com.easy.stazy.pgmanagement.pg.validations.RoomValidator;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the RoomService interface for managing room operations in the hostel module.
 * <p>
 * This service provides business logic for creating rooms associated with a specific floor in a PG (Paying Guest) accommodation.
 * It interacts with the RoomRepository and FloorRepository for persistence and uses RoomRequestMapper for converting DTOs to entities.
 * All data-modifying operations are transactional to ensure data consistency.
 * <p>
 * Typical use cases include:
 * <ul>
 *   <li>Creating a new room for a specific floor</li>
 * </ul>
 *
 */
@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;
    private final RoomRequestMapper roomRequestMapper;
    private final RoomValidator roomValidator;
    private final RentalOptionRepository rentalOptionRepository;

    /**
     * Creates a new room for a given floor.
     * <p>
     * This method maps the incoming RoomRequestDto to a RoomEntity, fetches the FloorEntity by the provided floorId,
     * sets the floor reference in the room entity, and persists the new room in the database. If the floor does not exist,
     * it throws an IllegalArgumentException. This operation is transactional to ensure data consistency.
     *
     * @param request the DTO containing room details (such as room number, type, etc.)
     * @param floorId the ID of the floor to which the room should be assigned
     * @throws IllegalArgumentException if the floor with the given ID does not exist
     */
    @Transactional
    public void createRoom(RoomRequestDto request, long floorId) {
        roomValidator.validateRoomUniqueness(floorId, request);
        RoomEntity entity = roomRequestMapper.toEntity(request);
        FloorEntity floor = floorRepository.findById(floorId).orElseThrow(() -> new ResourceNotFoundException("Floor not found"));
        entity.setFloor(floor);
        // Fetch rental options by pgId and sharing type using a native query
        Long pgId = floor.getPg().getId();
        List<RentalOptionEntity> rentalOptions = rentalOptionRepository.findByPgIdAndSharingTypeNative(pgId, request.getSharingType());
        if (rentalOptions.isEmpty()) {
            throw new ResourceNotFoundException("No rental options found for the specified PG and sharing type");
        }
        entity.setRentalOptions(rentalOptions);
        roomRepository.save(entity);
    }
}
