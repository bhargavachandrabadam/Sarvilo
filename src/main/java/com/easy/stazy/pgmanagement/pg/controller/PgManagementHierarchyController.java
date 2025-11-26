package com.easy.stazy.pgmanagement.pg.controller;

import com.easy.stazy.pgmanagement.pg.dto.response.PgManagementHierarchyResponse;
import com.easy.stazy.pgmanagement.pg.enums.HierarchyFilterStatus;
import com.easy.stazy.pgmanagement.pg.service.PgManagementHierarchyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(
        name = "PG Management Hierarchy Controller",
        description = "APIs for retrieving PG management hierarchy details"
)
@RestController
@RequestMapping("/v1/owner/pg")
@RequiredArgsConstructor
public class PgManagementHierarchyController {
    private final PgManagementHierarchyService hierarchyService;

    @Operation(
            summary = "Get PG Management Hierarchy",
            description = "Retrieves the hierarchical structure of a PG including floors, rooms, and tenants with optional filters."
    )
    @GetMapping("/hierarchy")
    public ResponseEntity<PgManagementHierarchyResponse> getHierarchy(
            @RequestParam Long pgId,
            @RequestParam(required = false) String floor,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false, defaultValue = "ALL") HierarchyFilterStatus status
    ) {
        return ResponseEntity.ok(
                hierarchyService.getPgHierarchy(pgId, floor, date, status)
        );
    }
}
