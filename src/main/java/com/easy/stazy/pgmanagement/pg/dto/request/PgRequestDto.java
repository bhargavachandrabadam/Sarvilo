package com.easy.stazy.pgmanagement.pg.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PgRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 6415060832808432561L;

    @NotNull
    private String name;

    @NotNull
    private String location;
    private String type;
}

