package com.easy.stazy.bookings.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "booking_request_proof_photos")
public class BookingRequestPhotoProofEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_request_id", nullable = false)
    private BookingRequestEntity bookingRequest;

    @Column(name = "proof_type", nullable = false)
    private String proofType;

    @Column(name = "proof_photo_url", nullable = false)
    private String proofPhotoUrl;
}

