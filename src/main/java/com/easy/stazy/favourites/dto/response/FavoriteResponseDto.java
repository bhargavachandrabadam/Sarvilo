package com.easy.stazy.favourites.dto.response;

import com.easy.stazy.pgmanagement.pg.entities.RentalOptionEntity;
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
public class FavoriteResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 2947377418149712935L;
    private Long pgId;
    private String pgName;
    private Long userId;
    private String location;
    private List<RentalOptionEntity> rentalOption;
    private int reviewsCount;
    private double avgRating;
}
