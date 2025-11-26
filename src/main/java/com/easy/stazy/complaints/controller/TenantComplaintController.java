package com.easy.stazy.complaints.controller;

import com.easy.stazy.complaints.dto.request.ComplaintRequestDto;
import com.easy.stazy.complaints.dto.response.ComplaintResponseDto;
import com.easy.stazy.complaints.service.ComplaintService;
import com.easy.stazy.shared.common.util.SuccessResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tenant/complaints")
@Tag(name = "Tenant Complaints", description = "Tenant-side complaint operations")
@RequiredArgsConstructor
public class TenantComplaintController {

    private final ComplaintService complaintService;

    @Operation(summary = "Submit a new complaint")
    @PostMapping("/{pgId}/{tenantId}")
    public ResponseEntity<SuccessResponseBody> createComplaint(
            @PathVariable Long pgId,
            @PathVariable Long tenantId,
            @RequestBody @Valid ComplaintRequestDto dto) {

        complaintService.createComplaint(pgId, tenantId, dto);
        return ResponseEntity.ok(new SuccessResponseBody("Complaint submitted successfully", null));
    }

    @Operation(summary = "Get complaints by tenant, PG, and status")
    @GetMapping("/{tenantId}/pg/{pgId}")
    public ResponseEntity<List<ComplaintResponseDto>> getComplaintsByTenantAndStatus(
            @PathVariable Long pgId,
            @PathVariable Long tenantId,
            @RequestParam(required = false) String status) {

        List<ComplaintResponseDto> complaints = complaintService.getComplaintsByTenantAndStatus(pgId, tenantId, status);
        return ResponseEntity.ok(complaints);
    }
}
