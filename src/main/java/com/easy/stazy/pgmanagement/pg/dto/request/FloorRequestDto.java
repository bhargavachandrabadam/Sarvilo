package com.easy.stazy.pgmanagement.pg.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FloorRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -345329113490272997L;

    @Schema(example = "First Floor")
    @NotNull
    private String floorNumber;
}
