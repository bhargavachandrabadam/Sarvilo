package com.easy.stazy.pgmanagement.pg.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PgHierarchyResponse {
    private Long pgId;
    private String pgName;
    private int NumberOfFloors;
    private List<FloorHierarchy> floors;
    private int numberOfOccupiedBedsInPgLevel;
    private int numberOfAvailableBedsInPgLevel;

    @Data
    public static class FloorHierarchy {
        private Long floorId;
        private String floorNumber;
        private int NumberOfRooms;
        private List<RoomHierarchy> rooms;
        private int numberOfOccupiedBedsInFloorLevel;
        private int numberOfAvailableBedsInFloorLevel;
    }

    @Data
    public static class RoomHierarchy {
        private Long roomId;
        private String roomNumber;
        private String type;
        private String description;
        private int NumberOfBeds;
        private List<BedHierarchy> beds;
        private int numberOfOccupiedBedsInRoomLevel;
        private int numberOfAvailableBedsInRoomLevel;
    }

    @Data
    public static class BedHierarchy {
        private Long bedId;
        private String bedNumber;
        private String description;
        private BigDecimal monthlyRent;
        private BedOccupancyHierarchy activeOccupancy;
        private boolean occupied;
    }

    @Data
    public static class BedOccupancyHierarchy {
        private Long bedOccupancyId;
        private String status;
        private LocalDate joiningDate;
        private LocalDate exitDate;
        private String notes;
        private Long tenantId;
        private String tenantName;
        private String tenantPhone;
        private String tenantEmail;
    }


}
