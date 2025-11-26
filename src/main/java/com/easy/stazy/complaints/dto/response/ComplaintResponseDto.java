package com.easy.stazy.complaints.dto.response;

import lombok.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -1217954563192631805L;

    private Long complaintId;
    private String tenantName;
    private String referenceNumber;
    private String floorNumber;
    private String roomNumber;
    private String bedNumber;
    private String description;
    private String status;
    private LocalDate createdAt;
}

