package com.easy.stazy.pgmanagement.pg.repository;

import com.easy.stazy.pgmanagement.pg.entities.FloorEntity;
import com.easy.stazy.pgmanagement.pg.entities.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    RoomEntity findByRoomNumberAndFloor(String roomNo, FloorEntity floor);

    @Query(value = "SELECT * FROM rooms WHERE room_number = :roomNumber AND floor_id = :floorId LIMIT 1", nativeQuery = true)
    RoomEntity findByRoomNumberAndFloorIdNative(@Param("roomNumber") String roomNumber, @Param("floorId") Long floorId);

    List<RoomEntity> findByFloorId(Long id);

    List<RoomEntity> findByFloorIdIn(List<Long> floorIds);

    RoomEntity findByFloorIdAndRoomNumber(Long id, String roomNumber);
}
