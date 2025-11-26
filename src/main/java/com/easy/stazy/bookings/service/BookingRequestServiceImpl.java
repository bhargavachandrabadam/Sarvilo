package com.easy.stazy.bookings.service;

import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.authentication.repository.UserRepository;
import com.easy.stazy.bookings.entity.BookingRequestEntity;
import com.easy.stazy.bookings.repository.BookingRequestRepository;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.bookings.dto.response.BookingListResponseDto;
import com.easy.stazy.bookings.dto.request.BookingRequestDto;
import com.easy.stazy.bookings.dto.response.BookingSummaryDto;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.bookings.enums.BookingStatus;
import com.easy.stazy.payments.enums.PaymentStatus;
import com.easy.stazy.pgmanagement.pg.enums.UserType;
import com.easy.stazy.bookings.handlers.BookingRequestPhotoHandler;
import com.easy.stazy.payments.projection.CautionDepositPaymentSummary;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.pgmanagement.tenant.service.TenantInfoService;
import com.easy.stazy.bookings.strategy.AllBookingsFetchStrategy;
import com.easy.stazy.bookings.strategy.BookingFetchStrategy;
import com.easy.stazy.bookings.strategy.RentalTypeBookingsFetchStrategy;
import com.easy.stazy.bookings.strategy.StatusBookingsFetchStrategy;
import com.easy.stazy.notifications.service.FcmNotificationService;
import com.easy.stazy.payments.entity.CautionDepositPaymentEntity;
import com.easy.stazy.payments.repository.CautionDepositPaymentRepository;
import com.easy.stazy.pgmanagement.tenant.repository.TenantInfoRepository;
import com.easy.stazy.reviews.repository.ReviewRepository;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import com.easy.stazy.shared.service.SecurityUserService;
import com.easy.stazy.pgmanagement.tenant.validations.TenantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingRequestServiceImpl implements BookingRequestService {

    private final PgManagementRepository pgManagementRepository;

    private final BookingRequestRepository bookingRequestRepository;

    private final UserRepository userRepository;

    private final SecurityUserService securityUserService;

    private final TenantInfoService tenantInfoService;

    private final BookingRequestPhotoHandler bookingRequestPhotoHandler;

    private final TenantInfoRepository tenantInfoRepository;

    private final TenantValidator tenantValidator;

    private final FcmNotificationService fcmNotificationService;

    private final ReviewRepository reviewRepository;

    private final CautionDepositPaymentRepository cautionDepositPaymentRepository;

    @Transactional
    @Override
    public void createBookingRequest(BookingRequestDto dto, MultipartFile profilePhoto) {
        PgManagementEntity pg = pgManagementRepository.findById(dto.getPgId())
                .orElseThrow(() -> new RuntimeException("PG not found"));
        UsersEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // if user is already an active tenant in this PG
        TenantInfoEntity tenantInfo = tenantInfoRepository.findByUserIdAndPgId(user.getId(), dto.getPgId())
                .get();
        if (tenantInfo != null) {
            boolean isActive = tenantInfoRepository.existsActiveOccupancyForUserAndPg(tenantInfo.getId(), dto.getPgId());
            if (isActive) {
                throw new RuntimeException("You are already an active tenant in this PG. Cannot create a new booking request until you vacate.");
            }
        }
        boolean exists = bookingRequestRepository.existsByPgAndUserAndStatus(pg, user, BookingStatus.PENDING);
        if (exists) {
            throw new RuntimeException("You have already booked this PG. Your booking is in pending status.");
        }
        BookingRequestEntity entity = getBookingRequestEntity(dto, pg, user, profilePhoto);
        bookingRequestRepository.save(entity);
    }

    private BookingRequestEntity getBookingRequestEntity(BookingRequestDto dto, PgManagementEntity pg, UsersEntity user, MultipartFile profilePhoto) {
        BookingRequestEntity entity = new BookingRequestEntity();
        entity.setTenantName(dto.getTenantName().trim());
        entity.setTenantContactNumber(dto.getTenantContactNumber().trim());
        entity.setEmailId(dto.getEmailId().trim());
        entity.setRentalType(dto.getRentalType().trim());
        entity.setSharingType(dto.getSharingType().trim());
        entity.setDateOfJoining(dto.getDateOfJoining());
        String url = bookingRequestPhotoHandler.handleProfilePhoto(entity, profilePhoto);
        entity.setTenantPhotoUrl(url);
        bookingRequestPhotoHandler.uploadProofPhotos(entity, dto.getProofPhotos());
        entity.setPg(pg);
        entity.setUser(user);
        return entity;
    }

    @Override
    public List<BookingRequestEntity> getBookingsByPgId(Long pgId) {
        return bookingRequestRepository.findByPgId(pgId);
    }

    @Override
    public List<BookingRequestEntity> getBookingsByUser(UsersEntity user) {
        return bookingRequestRepository.findByUser(user);
    }

    @Override
    public List<BookingSummaryDto> getBookingsByPgIdAndStatus(long pgId, BookingStatus status) {
        List<BookingSummaryDto> entities = bookingRequestRepository.findByPg_IdAndStatus(pgId, status);
        return entities != null ? entities : new ArrayList<>();
    }

    @Transactional
    @Override
    public void approveBooking(Long bookingId) {
        BookingRequestEntity booking = bookingRequestRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        String currentUserId = securityUserService.getCurrentUserId();
        boolean isOwner = securityUserService.hasRole("OWNER");
        long pgOwnerId = booking.getPg().getOwner().getId();
        if (currentUserId == null || !isOwner || !booking.getPg().getOwner().getId().equals(Long.valueOf(currentUserId))) {
            throw new AccessDeniedException("You are not authorized to approve this booking.");
        }
        booking.setStatus(BookingStatus.APPROVED);
        sendPushNotificationToUser(booking);
        tenantInfoService.createTenantAfterApproval(booking);
        bookingRequestRepository.save(booking);
    }

    private void sendPushNotificationToUser(BookingRequestEntity booking) {
        Map<String, String> dataPayLoad = Map.of(
                "tenantName", booking.getTenantName(),
                "pgName", booking.getPg().getPgName()
        );
        fcmNotificationService.sendNotificationAndLog(booking.getUser().getId(), UserType.TENANT,
                "Booking Approved",
                "Congratulations " + booking.getTenantName() + "! Your booking for " + booking.getPg().getPgName() + " has been approved.",
                dataPayLoad);
    }

    @Transactional
    @Override
    public void rejectBooking(Long bookingId) {
        BookingRequestEntity booking = bookingRequestRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        String currentUserId = securityUserService.getCurrentUserId();
        boolean isOwner = securityUserService.hasRole("OWNER");
        if (currentUserId == null || !isOwner || !booking.getPg().getOwner().getId().equals(Long.valueOf(currentUserId))) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to reject this booking.");
        }
        booking.setStatus(BookingStatus.REJECTED);
        bookingRequestRepository.save(booking);
    }

    /**
     * The getPgBookings(Long pgId, String status) method retrieves booking summaries for a given PG (paying guest accommodation) based on the provided status string:
     * If status is null, empty, "all", or "all statuses", it returns all booking summaries for the PG.
     * Otherwise, it normalizes the status string (trims and uppercases it).
     * If the normalized status is "PENDING ACTION", it is mapped to "PENDING".
     * It tries to match the normalized status to a BookingStatus enum:
     * If successful, it fetches bookings by PG ID and status.
     * If not (i.e., an IllegalArgumentException is thrown), it treats the status as a rental type and fetches bookings by PG ID and rental type.
     * This method provides flexible filtering of bookings by both status and rental type.
     *
     * @param pgId
     * @param status
     * @return
     */
    @Override
    public List<BookingSummaryDto> getPgBookings(Long pgId, String status) {
        BookingFetchStrategy strategy = getStrategy(status);
        List<BookingSummaryDto> result = strategy.fetch(pgId, status);
        setCautionDepositMessages(result);
        return result;
    }

    private void setCautionDepositMessages(List<BookingSummaryDto> result) {
        if (result == null || result.isEmpty()) return;
        List<Long> bookingIds = result.stream().map(BookingSummaryDto::getBookingId).toList();
        List<CautionDepositPaymentSummary> payments = cautionDepositPaymentRepository.findSummaryByBookingRequestIdIn(bookingIds);
        Map<Long, List<CautionDepositPaymentSummary>> paymentMap = payments.stream()
                .collect(java.util.stream.Collectors.groupingBy(CautionDepositPaymentSummary::getBookingRequestId));
        for (BookingSummaryDto dto : result) {
            List<CautionDepositPaymentSummary> bookingPayments = paymentMap.get(dto.getBookingId());
            if (bookingPayments != null && !bookingPayments.isEmpty()) {
                double totalPaid = 0.0;
                boolean paid = false;
                boolean partial = false;
                for (CautionDepositPaymentSummary p : bookingPayments) {
                    String paymentStatus = p.getStatus();
                    if ("PAID".equalsIgnoreCase(paymentStatus) || "PARTIALLY_PAID".equalsIgnoreCase(paymentStatus)) {
                        totalPaid += p.getAmountPaid() != null ? p.getAmountPaid().doubleValue() : 0.0;
                        if ("PAID".equalsIgnoreCase(paymentStatus)) paid = true;
                        if ("PARTIALLY_PAID".equalsIgnoreCase(paymentStatus)) partial = true;
                    }
                }
                if (paid) {
                    dto.setMessage("User paid caution deposit: Amount = " + totalPaid);
                } else if (partial) {
                    dto.setMessage("User marked partial caution deposit as paid: Amount = " + totalPaid);
                }
            }
        }
    }

    private BookingFetchStrategy getStrategy(String status) {
        String normalized = (status == null) ? "" : status.trim();
        if (normalized.isEmpty() ||
                normalized.equalsIgnoreCase("all") ||
                normalized.equalsIgnoreCase("all statuses")) {
            return new AllBookingsFetchStrategy(bookingRequestRepository);
        }
        String upper = normalized.toUpperCase();
        if ("PENDING ACTION".equals(upper)) {
            upper = "PENDING";
        }
        try {
            BookingStatus.valueOf(upper);
            return new StatusBookingsFetchStrategy(bookingRequestRepository);
        } catch (IllegalArgumentException ex) {
            return new RentalTypeBookingsFetchStrategy(bookingRequestRepository);
        }
    }

    @Override
    public List<BookingListResponseDto> getBookingsByUserIdAndStatus(Long userId, BookingStatus status) {
        List<BookingRequestEntity> bookings = bookingRequestRepository.findByUser_IdAndStatus(userId, status);
        List<Long> pgIds = extractPgIds(bookings);
        Map<Long, Double> avgRatings = fetchAvgRatings(pgIds);
        Map<Long, Long> reviewCounts = fetchReviewCounts(pgIds);
        List<BookingListResponseDto> result = new ArrayList<>();
        for (BookingRequestEntity booking : bookings) {
            BookingListResponseDto dto = mapToBookingListResponseDto(booking, avgRatings, reviewCounts);
            result.add(dto);
        }
        return result;
    }

    private List<Long> extractPgIds(List<BookingRequestEntity> bookings) {
        return bookings.stream().map(b -> b.getPg().getId()).distinct().toList();
    }

    private Map<Long, Double> fetchAvgRatings(List<Long> pgIds) {
        Map<Long, Double> avgRatings = new java.util.HashMap<>();
        for (Object[] row : reviewRepository.findAvgRatingsByPgIds(pgIds)) {
            avgRatings.put((Long) row[0], ((Number) row[1]).doubleValue());
        }
        return avgRatings;
    }

    private Map<Long, Long> fetchReviewCounts(List<Long> pgIds) {
        Map<Long, Long> reviewCounts = new java.util.HashMap<>();
        for (Object[] row : reviewRepository.findCountsByPgIds(pgIds)) {
            reviewCounts.put((Long) row[0], ((Number) row[1]).longValue());
        }
        return reviewCounts;
    }

    private BookingListResponseDto mapToBookingListResponseDto(BookingRequestEntity booking, Map<Long, Double> avgRatings, Map<Long, Long> reviewCounts) {
        PgManagementEntity pg = booking.getPg();
        BookingListResponseDto dto = mapBasicBookingDetails(booking, pg, avgRatings, reviewCounts);
        mapCautionDepositDetails(dto, booking.getId());
        return dto;
    }

    private BookingListResponseDto mapBasicBookingDetails(BookingRequestEntity booking, PgManagementEntity pg, Map<Long, Double> avgRatings, Map<Long, Long> reviewCounts) {
        Double avgRating = avgRatings.getOrDefault(pg.getId(), 0.0);
        long reviewsCount = reviewCounts.getOrDefault(pg.getId(), 0L);
        BookingListResponseDto dto = new BookingListResponseDto();
        dto.setPgId(pg.getId());
        dto.setPgName(pg.getPgName());
        dto.setPgLocation(pg.getLocation());
        dto.setRatingAvgCount(avgRating);
        dto.setReviewsCount((int) reviewsCount);
        dto.setTenantName(booking.getTenantName());
        dto.setRentalType(booking.getRentalType());
        dto.setDateOfJoining(booking.getDateOfJoining());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    private void mapCautionDepositDetails(BookingListResponseDto dto, Long bookingId) {
        List<CautionDepositPaymentEntity> cautionPayments = cautionDepositPaymentRepository.findByBookingRequestId(bookingId);
        if (cautionPayments != null && !cautionPayments.isEmpty()) {
            BookingListResponseDto.CautionDepositDetailsDto cautionDepositDetails = new BookingListResponseDto.CautionDepositDetailsDto();
            CautionDepositPaymentEntity latestPayment = cautionPayments.get(cautionPayments.size() - 1);
            cautionDepositDetails.setAmountPaid(latestPayment.getAmountPaid().doubleValue());
            cautionDepositDetails.setPaymentDate(java.sql.Timestamp.valueOf(latestPayment.getPaymentDate()));
            cautionDepositDetails.setPaymentStatus(latestPayment.getStatus() != null ? PaymentStatus.valueOf(latestPayment.getStatus()) : null);
            dto.setCautionDepositDetails(cautionDepositDetails);
        }
    }
}
