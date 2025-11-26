package com.easy.stazy.bookings.repository;

import com.easy.stazy.bookings.dto.response.BookingSummaryDto;
import com.easy.stazy.bookings.entity.BookingRequestEntity;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.bookings.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRequestRepository extends JpaRepository<BookingRequestEntity, Long> {
    List<BookingRequestEntity> findByPgId(Long pgId);
    List<BookingRequestEntity> findByUser(UsersEntity user);
    boolean existsByPgAndUserAndStatus(PgManagementEntity pg, UsersEntity user, BookingStatus status);
    List<BookingRequestEntity> findByPgAndStatus(PgManagementEntity pg, BookingStatus status);

    @Query("SELECT new com.easy.stazy.bookings.dto.response.BookingSummaryDto(b.pg.id, b.tenantName, b.tenantContactNumber, b.rentalType, b.dateOfJoining, b.id, b.pg.pgName, b.status) FROM BookingRequestEntity b WHERE b.pg.id = :pgId AND b.status = :status")
    List<BookingSummaryDto> findByPg_IdAndStatus(long pgId, BookingStatus status);

    @Query("SELECT new com.easy.stazy.bookings.dto.response.BookingSummaryDto(b.pg.id, b.tenantName, b.tenantContactNumber, b.rentalType, b.dateOfJoining, b.id, b.pg.pgName, b.status) FROM BookingRequestEntity b WHERE b.pg.id = :pgId")
    List<BookingSummaryDto> findSummaryByPgId(Long pgId);

    List<BookingRequestEntity> findByPg_Id(Long pgId);
    @Query("SELECT new com.easy.stazy.bookings.dto.response.BookingSummaryDto(b.pg.id, b.tenantName, b.tenantContactNumber, b.rentalType, b.dateOfJoining, b.id, b.pg.pgName, b.status) FROM BookingRequestEntity b WHERE b.pg.id = :pgId AND b.rentalType = :rentalType")
    List<BookingSummaryDto> findByPgIdAndRentalType(Long pgId, String rentalType);
    List<BookingRequestEntity> findByUser_IdAndStatus(Long userId, BookingStatus status);

    BookingRequestEntity findTopByUserIdAndPgIdOrderByCreatedAtDesc(Long userId, Long pgId);

    BookingRequestEntity findByPgIdAndStatus(Long pgId, BookingStatus bookingStatus);

    Optional<BookingRequestEntity> findByIdAndStatus(Long pgId, BookingStatus bookingStatus);
}
