package com.easy.stazy.pgmanagement.pg.controller;

import com.easy.stazy.pgmanagement.pg.dto.request.BedRequestDto;
import com.easy.stazy.pgmanagement.pg.dto.request.FloorRequestDto;
import com.easy.stazy.pgmanagement.pg.dto.request.RoomRequestDto;
import com.easy.stazy.pgmanagement.pg.dto.response.HostelDetailsResponseDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgHierarchyResponse;
import com.easy.stazy.pgmanagement.pg.service.*;
import com.easy.stazy.shared.common.util.SuccessResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Owner Hostel Management", description = "Owner-side hostel operations")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/owner")
public class HostelController {

    private final HostelDetailsService hostelDetailsService;
    private final PgHeirarchyService pgHeirarchyService;
    private final FloorService floorService;
    private final RoomService roomService;
    private final BedService bedService;

    @GetMapping("/details")
    public ResponseEntity<SuccessResponseBody> getHostelDetails(@Valid @RequestParam Long pgId){
        HostelDetailsResponseDto response = hostelDetailsService.getHostelDetails(pgId);
        SuccessResponseBody responseBody = new SuccessResponseBody("Hostel details fetched successfully", response);
        return ResponseEntity.ok(responseBody);
    }



    @Operation(summary = "Create floor for a PG", description = "Creates a new floor under the specified PG.")
    @PostMapping("/floor/{pgId}")
    public ResponseEntity<SuccessResponseBody> createFloor(@Valid @PathVariable long pgId, @RequestBody FloorRequestDto request) {
        floorService.createFloor(request, pgId);
        return ResponseEntity.ok(new SuccessResponseBody("Floor created successfully", null));
    }

    @Operation(summary = "Create room for a floor", description = "Creates a new room under the specified floor.")
    @PostMapping("/room/{floorId}")
    public ResponseEntity<SuccessResponseBody> createRoom(@Valid @PathVariable long floorId, @RequestBody RoomRequestDto request) {
        roomService.createRoom(request, floorId);
        return ResponseEntity.ok(new SuccessResponseBody("Room created successfully", null));
    }

    @Operation(summary = "Create bed for a room", description = "Creates a new bed under the specified room.")
    @PostMapping("/bed/{roomId}")
    public ResponseEntity<SuccessResponseBody> createBed(@Valid @PathVariable long roomId, @RequestBody BedRequestDto request) {
        bedService.createBed(request, roomId);
        return ResponseEntity.ok(new SuccessResponseBody("Bed created successfully", null));
    }

    @Operation(summary = "Remove bed", description = "Removes a bed by its ID.")
    @DeleteMapping("/bed/{bedId}")
    public ResponseEntity<SuccessResponseBody> removeBed(@PathVariable Long bedId) {
        bedService.removeBed(bedId);
        return ResponseEntity.ok(new SuccessResponseBody("Bed removed successfully", null));
    }

    @Operation(summary = "Get PG hierarchy", description = "Fetches the complete hierarchy of a PG including floors, rooms, and beds, bed occupancy.")
    @GetMapping("/pg-hierarchy/{pgId}")
    public ResponseEntity<SuccessResponseBody> getPgHierarchy(@PathVariable Long pgId) {
        PgHierarchyResponse response = pgHeirarchyService.getPgHierarchy(pgId);
        return ResponseEntity.ok(new SuccessResponseBody("PG hierarchy fetched successfully", response));
    }


}
