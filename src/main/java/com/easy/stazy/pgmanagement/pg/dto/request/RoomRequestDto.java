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
public class RoomRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1376407771355321293L;

    @NotNull
    @Schema(example = "101")
    private String roomNumber;

    @Schema(example = "Deluxe Suite")
    private String description;

    @NotNull
    private String sharingType;

}
