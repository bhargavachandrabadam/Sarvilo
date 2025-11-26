package com.easy.stazy.pgmanagement.pg.controller;

import com.easy.stazy.pgmanagement.pg.dto.request.RentalOptionRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.RentalOptionEntity;
import com.easy.stazy.pgmanagement.pg.service.RentalOptionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/rental-options")
@RequiredArgsConstructor
public class RentalOptionController {

    private final RentalOptionService rentalOptionService;

    @Operation(summary = "Create a new rental option for a PG", description = "Creates a new rental option associated with the specified PG ID.")
    @PostMapping("/{pgId}")
    public ResponseEntity<RentalOptionEntity> createRentalOption(@PathVariable Long pgId, @RequestBody RentalOptionRequestDto dto) {
        RentalOptionEntity created = rentalOptionService.createRentalOption(pgId, dto);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Update an existing rental option", description = "Updates the rental option identified by the given ID with new details.")
    @PutMapping("/{id}")
    public ResponseEntity<RentalOptionEntity> updateRentalOption(@PathVariable Long id, @RequestBody RentalOptionRequestDto dto) {
        RentalOptionEntity updated = rentalOptionService.updateRentalOption(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
}

