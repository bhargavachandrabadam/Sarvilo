package com.easy.stazy.pgmanagement.pg.dto.response;

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
public class PgDetailsWithPhotosResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -6519141958652845905L;
    private PgDetailsResponseDto pgDetails;
    private List<String> photoPaths;

}

