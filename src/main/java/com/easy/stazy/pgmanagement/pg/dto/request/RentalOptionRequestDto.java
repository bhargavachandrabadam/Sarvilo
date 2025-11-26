package com.easy.stazy.pgmanagement.pg.dto.request;

import com.easy.stazy.pgmanagement.pg.enums.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for rental option creation, including rental type, sharing type, and rent.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RentalOptionRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 5452544412437091290L;

    @Schema(example = "MONTHLY")
    private RentalType rentalType;
    @Schema(example = "2 sharing")
    private String sharingType;
    @Schema(example = "6000.00")
    private BigDecimal rent;
    @Schema(example = "1000.00")
    private BigDecimal cautionDeposit;
}
