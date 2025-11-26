package com.easy.stazy.pgmanagement.pg.validations.impl;

import com.easy.stazy.pgmanagement.pg.dto.request.RoomRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.RoomEntity;
import com.easy.stazy.pgmanagement.pg.repository.RoomRepository;
import com.easy.stazy.pgmanagement.pg.validations.RoomValidator;
import com.easy.stazy.shared.common.exception.AlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of RoomValidator to ensure no duplicate room exists in a floor of a PG.
 */
@Service
@RequiredArgsConstructor
public class RoomValidatorImpl implements RoomValidator {

    private final RoomRepository roomRepository;

    /**
     * Checks if a room with the same number already exists in the given floor and PG.
     * Throws AlreadyExistsException if duplicate found.
     *
     * @param floorId the floor id
     * @param dto     the RoomRequestDto containing room details
     * @throws AlreadyExistsException if a duplicate room exists
     */
    @Override
    public void validateRoomUniqueness(Long floorId, RoomRequestDto dto) {
        RoomEntity existingRoom = roomRepository.findByRoomNumberAndFloorIdNative(dto.getRoomNumber(), floorId);
        if (existingRoom != null) {
            throw new AlreadyExistsException("Room with number '" + dto.getRoomNumber() + "' already exists in this floor of the PG.");
        }
    }
}
