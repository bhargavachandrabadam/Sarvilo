package com.easy.stazy.reviews.dto.response;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -6982150226502255734L;
    private String tenantName;
    private String title;
    private String description;
    private Double rating;
    private LocalDateTime updatedAt;
}

