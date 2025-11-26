package com.easy.stazy.pgmanagement.pg.service;

import com.easy.stazy.pgmanagement.pg.dto.request.BedOccupancyUpdateRequestDto;
import com.easy.stazy.pgmanagement.pg.dto.request.BedRequestDto;

/**
 * Implementation of Bed-level operations.
 */

public interface BedService {

     void createBed(BedRequestDto request, long roomId);

     void updateExitDate(Long tenantId, BedOccupancyUpdateRequestDto request);

    void deleteExitDate(Long tenantId);

    void removeBed(Long bedId);
}

