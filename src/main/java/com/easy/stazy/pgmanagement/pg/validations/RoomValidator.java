package com.easy.stazy.pgmanagement.pg.validations;

import com.easy.stazy.pgmanagement.pg.dto.request.RoomRequestDto;

/**
 * Validator interface for Room entity to ensure no duplicate room exists in a floor of a PG.
 */
public interface RoomValidator {
    /**
     * Validates that a room with the given room number does not already exist in the specified floor and PG.
     *
     * @param pgId    the PG id
     * @param floorId the floor id
     * @param dto     the RoomRequestDto containing room details
     * @throws IllegalArgumentException if a duplicate room exists
     */
    void validateRoomUniqueness(Long floorId, RoomRequestDto dto);
}

