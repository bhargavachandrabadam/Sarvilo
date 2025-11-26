package com.easy.stazy.pgmanagement.pg.service.impl;

import com.easy.stazy.pgmanagement.pg.dto.request.BedOccupancyUpdateRequestDto;
import com.easy.stazy.pgmanagement.pg.dto.request.BedRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.BedEntity;
import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.pg.entities.RoomEntity;
import com.easy.stazy.pgmanagement.pg.enums.BedOccupancyStatus;
import com.easy.stazy.pgmanagement.pg.mapper.requests.BedRequestMapper;
import com.easy.stazy.pgmanagement.pg.repository.BedOccupancyRepository;
import com.easy.stazy.pgmanagement.pg.repository.BedRepository;
import com.easy.stazy.pgmanagement.pg.repository.RoomRepository;
import com.easy.stazy.pgmanagement.pg.service.BedService;
import com.easy.stazy.pgmanagement.pg.validations.BedValidator;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Implementation of the BedService interface for managing bed and bed occupancy operations in the hostel module.
 * <p>
 * This service provides business logic for creating beds, updating bed occupancy exit dates and notes, and other
 * bed-related operations. It interacts with the BedRepository, RoomRepository, and BedOccupancyRepository to
 * persist and retrieve data from the database. The service also uses BedRequestMapper to convert between DTOs
 * and entity objects. All operations that modify data are transactional to ensure data consistency.
 * <p>
 * Typical use cases include:
 * <ul>
 *   <li>Creating a new bed for a specific room</li>
 *   <li>Updating the exit date and notes for a bed occupancy record</li>
 *   <li>Other bed management operations as defined in the BedService interface</li>
 * </ul>
 *
 */
@RequiredArgsConstructor
@Service

public class BedServiceImpl implements BedService {
    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final BedOccupancyRepository bedOccupancyRepository;
    private final BedRequestMapper bedRequestMapper;
    private final BedValidator bedValidator;

    private static BedEntity getBedEntity(BedOccupancyUpdateRequestDto request, BedOccupancyEntity entity) {
        BedEntity bedEntity = entity.getBed();
        // Directly set the custom notes as provided in the request, without mapping to enums or constants
        String note = request.getNotes();
        if (note != null) {
            if (note.equalsIgnoreCase("Under Maintenance")) {
                bedEntity.setNotes("UNDER_MAINTENANCE");
            } else if (note.equalsIgnoreCase("Ready to Occupy")) {
                bedEntity.setNotes("READY_TO_OCCUPY");
            } else if (note.equalsIgnoreCase("Already Booked")) {
                bedEntity.setNotes("OCCUPIED");
            } else {
                bedEntity.setNotes(note);
            }
        }
        return bedEntity;
    }

    /**
     * Creates a new bed for a given room.
     * Validates that no duplicate bed number exists in the room before saving.
     *
     * @param request the DTO containing bed details (such as bed number, type, etc.)
     * @param roomId  the ID of the room to which the bed belongs
     */
    @Transactional
    @Override
    public void createBed(BedRequestDto request, long roomId) {
        bedValidator.validateBedUniqueness(roomId, request);
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));
        BedEntity bed = bedRequestMapper.toEntity(request);
        bed.setRoom(room);
        bedRepository.save(bed);
    }

    /**
     * Updates the exit date and notes for a specific bed occupancy record.
     * <p>
     * This method retrieves the BedOccupancyEntity by its ID, updates the exit date and notes fields
     * using the values from the provided BedOccupancyUpdateRequestDto, and saves the updated entity.
     * If the bed occupancy record does not exist, it throws an Resource Not Found Exception. This operation
     * is transactional to ensure data consistency.
     *
     * @param tenantId the ID of the bed occupancy record to update
     * @param request  the DTO containing the new exit date and notes
     * @throws ResourceNotFoundException if the bed occupancy record with the given ID does not exist
     */
    @Transactional
    @Override
    public void updateExitDate(Long tenantId, BedOccupancyUpdateRequestDto request) {

        BedOccupancyEntity entity = bedOccupancyRepository.findByTenant_IdAndStatus(tenantId, BedOccupancyStatus.ACTIVE.name())
                .orElseThrow(() -> new ResourceNotFoundException("Bed occupancy not found"));
        entity.setExitDate(request.getExitDate());
        BedEntity bedEntity = getBedEntity(request, entity);
        bedRepository.save(bedEntity);
        // Update status to VACATED if exit date is today or in the past
        if (request.getExitDate() != null && !request.getExitDate().isAfter(LocalDate.now())) {
            entity.setStatus(BedOccupancyStatus.VACATED.name());
        }
        bedOccupancyRepository.save(entity);
    }

    @Transactional
    @Override
    public void deleteExitDate(Long tenantId) {
        BedOccupancyEntity entity = bedOccupancyRepository.findByTenant_IdAndStatus(tenantId, BedOccupancyStatus.ACTIVE.name())
                .orElseThrow(() -> new ResourceNotFoundException("Bed occupancy not found"));
        entity.setExitDate(null);
        entity.setNotes(null);
        bedOccupancyRepository.save(entity);
    }

    @Transactional
    @Override
    public void removeBed(Long bedId) {
        BedEntity bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found with id: " + bedId));
        if (bed.getBedOccupancies().stream().anyMatch(bedOccupancyEntity -> bedOccupancyEntity.getStatus().equals(BedOccupancyStatus.ACTIVE.name()))) {
            throw new IllegalStateException("Cannot remove bed with active occupancy");
        }
        bed.setIsDeleted(true);
        bedRepository.save(bed);
    }
}
