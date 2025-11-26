package com.easy.stazy.pgmanagement.admin.service.specification;

import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.entities.RentalOptionEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class PgSpecification {
    public static Specification<PgManagementEntity> filter(
            Double minPrice,
            Double maxPrice,
            List<String> amenities,
            List<String> sharingType,
            String gender,
            List<String> places
    ) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            Predicate predicate = cb.conjunction(); // Start with AND logic
            List<Predicate> andPredicates = new java.util.ArrayList<>();
            if (gender != null) {
                andPredicates.add(cb.equal(cb.lower(root.get("genderChoice")), gender.toLowerCase()));
            }
            if (amenities != null && !amenities.isEmpty()) {
                Join<PgManagementEntity, ?> amenityJoin = root.join("amenities", JoinType.LEFT);
                andPredicates.add(amenityJoin.get("name").in(amenities));
            }
            if (sharingType != null && !sharingType.isEmpty()) {
                Join<PgManagementEntity, RentalOptionEntity> rentalJoin = root.join("rentalOptions", JoinType.LEFT);
                andPredicates.add(rentalJoin.get("sharingType").in(sharingType));
            } else if (minPrice != null || maxPrice != null) {
                Join<PgManagementEntity, RentalOptionEntity> rentalJoin = root.join("rentalOptions", JoinType.LEFT);
                if (minPrice != null) {
                    andPredicates.add(cb.ge(rentalJoin.get("price"), minPrice));
                }
                if (maxPrice != null) {
                    andPredicates.add(cb.le(rentalJoin.get("price"), maxPrice));
                }
            }
            if (places != null && !places.isEmpty()) {
                List<Predicate> placePredicates = new java.util.ArrayList<>();
                for (String place : places) {
                    String placeLower = place.toLowerCase();
                    placePredicates.add(cb.equal(cb.lower(root.get("state")), placeLower));
                    placePredicates.add(cb.equal(cb.lower(root.get("district")), placeLower));
                    placePredicates.add(cb.equal(root.get("pinCode"), place));
                    placePredicates.add(cb.like(cb.lower(root.get("location")), "%" + placeLower + "%"));
                }
                andPredicates.add(cb.or(placePredicates.toArray(new Predicate[0])));
            }
            if (!andPredicates.isEmpty()) {
                predicate = cb.and(andPredicates.toArray(new Predicate[0]));
            }
            return predicate;
        };
    }
}
