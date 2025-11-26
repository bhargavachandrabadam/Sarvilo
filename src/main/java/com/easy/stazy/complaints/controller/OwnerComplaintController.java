package com.easy.stazy.complaints.controller;

import com.easy.stazy.complaints.service.ComplaintService;
import com.easy.stazy.shared.common.util.SuccessResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/v1/owner/complaints")
@Tag(name = "Owner Complaints", description = "Owner-side complaint management")
@RequiredArgsConstructor
public class OwnerComplaintController {

    private final ComplaintService complaintService;

    @Operation(summary = "Get all complaints by PG")
    @GetMapping("/{pgId}")
    public ResponseEntity<SuccessResponseBody> getAllComplaintsByPg(@PathVariable Long pgId,@RequestParam String statusFilter) {
        return ResponseEntity.ok(new SuccessResponseBody("Complaints fetched successfully",
                complaintService.getAllComplaintsByPg(pgId,statusFilter)));
    }

    @Operation(summary = "Update complaint status")
    @PatchMapping("/{complaintId}/status")
    public ResponseEntity<SuccessResponseBody> updateComplaintStatus(
            @PathVariable Long complaintId,
            @RequestParam String status) {

        complaintService.updateComplaintStatus(complaintId, status);
        return ResponseEntity.ok(new SuccessResponseBody("Complaint status updated successfully", null));
    }
}
