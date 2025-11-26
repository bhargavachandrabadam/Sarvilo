package com.easy.stazy.pgmanagement.pg.service;

import com.easy.stazy.pgmanagement.pg.dto.response.PgManagementHierarchyResponse;
import com.easy.stazy.pgmanagement.pg.enums.HierarchyFilterStatus;

import java.time.LocalDate;

public interface PgManagementHierarchyService {
    PgManagementHierarchyResponse getPgHierarchy(Long pgId, String floor, LocalDate date, HierarchyFilterStatus status);
}

