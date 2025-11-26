package com.easy.stazy.reviews.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -2498963313188881715L;
    @Schema(example = "Great stay!")
    private String title;
    @Schema(example = "Clean rooms and friendly staff.")
    private String description;
    @Schema(example = "4.5")
    private Double rating;
}
