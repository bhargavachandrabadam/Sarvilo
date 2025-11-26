package com.easy.stazy.complaints.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class ComplaintRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 7776313002573824213L;

    @Schema(example = "101")
    @NotNull
    private String roomNumber;
    @Schema(example = "A1")
    @NotNull
    private String bedNumber;
    @Schema(example = "1")
    @NotNull
    private String floor;
    @Schema(example = "AC not working")
    @NotNull
    private String description;

}
