package com.easy.stazy.pgmanagement.pg.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BedOccupancyUpdateRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -8805211043955661791L;

    @Schema(example = "2025-10-23")
    @NotNull
    private LocalDate exitDate;

    @NotNull
    @Schema(example = "Tenant vacated the bed.")
    private String notes;


}
