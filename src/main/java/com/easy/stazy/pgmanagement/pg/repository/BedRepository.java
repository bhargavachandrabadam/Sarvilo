package com.easy.stazy.pgmanagement.pg.repository;

import com.easy.stazy.pgmanagement.pg.entities.BedEntity;
import com.easy.stazy.pgmanagement.pg.entities.RoomEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BedRepository extends JpaRepository<BedEntity, Long> {

    // Find bed by bed number and room
    BedEntity findByBedNumberAndRoom(String bedNo, RoomEntity room);

    // Find bed by floor number, room number, bed number and pg id
    @Query("SELECT b FROM BedEntity b JOIN b.room r JOIN r.floor f JOIN f.pg p " +
            "WHERE f.floorNumber = :floorNumber AND r.roomNumber = :roomNumber AND b.bedNumber = :bedNumber AND p.id = :pgId")
    Optional<BedEntity> findBedByDetails(String floorNumber, String roomNumber, String bedNumber, Long pgId);

    // Find bed by bed number and room id
    BedEntity findByBedNumberAndRoomId(String bedNo, Long roomId);

    // Count total beds for a PG
    @Query("SELECT COUNT(b) FROM BedEntity b JOIN b.room r JOIN r.floor f JOIN f.pg p WHERE p.id = :pgId AND b.isDeleted = false")
    long countByPgId(Long pgId);

    // Find beds by room id
    List<BedEntity> findByRoomId(Long id);

    // Bulk fetch beds by room ids to avoid N+1
    List<BedEntity> findByRoomIdIn(List<Long> roomIds);

    // Finds bed by room id and bed number
    BedEntity findByRoomIdAndBedNumber(Long id, String bedNumber);

    // Count beds by room
    int countByRoom(RoomEntity room);

    // Find bed by bed number, room id and isDeleted flag
    BedEntity findByBedNumberAndRoomIdAndIsDeleted(@NotNull String bedNumber, Long roomId, boolean b);

    BedEntity findByRoomIdAndBedNumberAndIsDeleted(Long id, String bedNumber, boolean b);
}
