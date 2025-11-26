package com.easy.stazy.complaints.service;


import com.easy.stazy.complaints.dto.request.ComplaintRequestDto;
import com.easy.stazy.complaints.dto.response.ComplaintResponseDto;

import java.util.List;


public interface ComplaintService {


     void createComplaint(long pgId, long tenantId, ComplaintRequestDto dto);

     void updateComplaintStatus(long complaintId, String status);

    /**
     * Get complaints for a tenant in a PG filtered by status (optional).
     * @param pgId the PG ID
     * @param tenantId the tenant ID
     * @param status the complaint status (optional)
     * @return list of complaints
     */
    List<ComplaintResponseDto> getComplaintsByTenantAndStatus(Long pgId, Long tenantId, String status);
    List<ComplaintResponseDto> getAllComplaintsByPg(long pgId, String statusFilter);
}
