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
public class BedRequestDto implements Serializable{

    @Serial
    private static final long serialVersionUID = 7093123814310924591L;

    @Schema(example = "A1")
    @NotNull
    private String bedNumber;

    @Schema(example = "Single bed near window")
    private String description;
}
