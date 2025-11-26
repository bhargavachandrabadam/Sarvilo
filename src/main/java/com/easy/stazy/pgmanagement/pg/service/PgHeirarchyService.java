package com.easy.stazy.pgmanagement.pg.service;

import com.easy.stazy.pgmanagement.pg.dto.response.PgHierarchyResponse;
import com.easy.stazy.pgmanagement.pg.entities.BedEntity;
import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.pg.entities.FloorEntity;

public interface PgHeirarchyService {

    PgHierarchyResponse getPgHierarchy(Long pgId);

    PgHierarchyResponse.FloorHierarchy getFloorHierarchy(FloorEntity floor, PgHierarchyResponse.FloorHierarchy floorDto);

    PgHierarchyResponse.BedHierarchy getBedHierarchy(BedEntity bed);

    PgHierarchyResponse.BedOccupancyHierarchy getBedOccupancyHierarchy(BedOccupancyEntity active);


}
