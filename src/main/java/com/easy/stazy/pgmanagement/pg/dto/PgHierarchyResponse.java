package com.easy.stazy.pgmanagement.pg.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PgHierarchyResponse {
    private Long pgId;
    private String pgName;
    private int numberOfFloors;
    private List<FloorHierarchy> floors;
    private int numberOfOccupiedBedsInPgLevel;
    private int numberOfAvailableBedsInPgLevel;

    @Data
    public static class FloorHierarchy {
        private Long floorId;
        private String floorNumber;
        private int numberOfRooms;
        private List<RoomHierarchy> rooms;
        private int numberOfOccupiedBedsInFloorLevel;
        private int numberOfAvailableBedsInFloorLevel;
    }

    @Data
    public static class RoomHierarchy {
        private Long roomId;
        private String roomNumber;
        private String description;
        private int numberOfBeds;
        private List<BedHierarchy> beds;
        private int numberOfOccupiedBedsInRoomLevel;
        private int numberOfAvailableBedsInRoomLevel;
    }

    @Data
    public static class BedHierarchy {
        private Long bedId;
        private String bedNumber;
        private String description;
        private BedOccupancyHierarchy activeOccupancy;
        private boolean occupied;
        private boolean underMaintenance;
        private boolean readyToOccupy;
        private boolean inNoticePeriod;
        private boolean assigned;
        private String notes;
        private RentStatus rentStatus;
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
        private String rentalType;
        private String sharingType;
    }

    public enum RentStatus {
        PAID,
        UNPAID,
        DAILY_RENT,
        NOT_APPLICABLE
    }
}

