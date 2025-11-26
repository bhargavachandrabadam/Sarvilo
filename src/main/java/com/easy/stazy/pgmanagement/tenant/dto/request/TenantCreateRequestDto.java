package com.easy.stazy.pgmanagement.tenant.dto.request;

import com.easy.stazy.pgmanagement.pg.enums.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantCreateRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -3666769487667669608L;
    @Schema(example = "John Doe")
    @NotBlank
    private String name;
    @Schema(example = "9876543210")
    @NotBlank
    private String contactNumber;
    @Schema(example = "john.doe@example.com")
    private String emailId;
    @Schema(example = "1")
    private String floorNumber;
    @Schema(example = "101")

    private String roomNumber;
    @Schema(example = "A1")

    private String bedNumber;
    @NotBlank
    @NotNull
    @Schema(example = "MONTHLY")
    private RentalType rentalType;

    @NotBlank
    @NotNull
    private String sharingType;

    private List<TenantProofDto> tenantProofs;
}
