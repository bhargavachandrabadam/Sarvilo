package com.easy.stazy.pgmanagement.pg.enums;

public enum BedOccupancyStatus {
    ACTIVE, //The tenant is currently occupying the bed
    INACTIVE, //The occupancy is not currently active (could be used for past occupancies).
    VACATED, //The tenant has left and the bed is now vacant.
    PENDING, //The occupancy is scheduled but not yet started.
    TERMINATED //The occupancy has been terminated before the expected exit date.
}

