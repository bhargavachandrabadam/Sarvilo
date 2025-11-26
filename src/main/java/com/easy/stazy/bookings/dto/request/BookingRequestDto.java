package com.easy.stazy.bookings.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.easy.stazy.pgmanagement.tenant.dto.request.TenantProofDto;

@Data
public class BookingRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 2149763240749980648L;

    @NotNull
    private String tenantName;
    @NotNull
    private String tenantContactNumber;
    @NotNull
    private String emailId;
    @NotNull
    private String rentalType;
    @NotNull
    private LocalDate dateOfJoining;
    private List<TenantProofDto> proofPhotos;
    @NotNull
    private Long pgId;
    @NotNull
    private Long userId;
    @NotNull
    private String sharingType;
}
