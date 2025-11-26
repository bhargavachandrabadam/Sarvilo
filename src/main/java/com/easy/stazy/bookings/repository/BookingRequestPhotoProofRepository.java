package com.easy.stazy.bookings.repository;

import com.easy.stazy.bookings.entity.BookingRequestPhotoProofEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRequestPhotoProofRepository extends JpaRepository<BookingRequestPhotoProofEntity, Long> {
    // Add custom query methods if needed
}

