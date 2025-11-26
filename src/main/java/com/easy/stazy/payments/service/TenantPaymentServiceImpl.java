package com.easy.stazy.payments.service;

import com.easy.stazy.pgmanagement.pg.entities.BedEntity;
import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.pg.entities.RentalOptionEntity;
import com.easy.stazy.pgmanagement.pg.entities.RoomEntity;
import com.easy.stazy.payments.dto.request.TenantPaymentRequestDto;
import com.easy.stazy.payments.dto.response.TenantRentPaymentsDto;
import com.easy.stazy.payments.dto.response.TenantRentsResponseDto;
import com.easy.stazy.pgmanagement.pg.enums.BedOccupancyStatus;
import com.easy.stazy.payments.enums.PaymentMode;
import com.easy.stazy.payments.enums.PaymentStatus;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.pgmanagement.tenant.repository.TenantInfoRepository;
import com.easy.stazy.payments.repository.TenantPaymentRepository;
import com.easy.stazy.payments.validation.TenantPaymentValidator;
import com.easy.stazy.payments.validation.OwnerPaymentValidator;
import com.easy.stazy.payments.entity.TenantPaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class TenantPaymentServiceImpl implements TenantPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(TenantPaymentServiceImpl.class);

    private final TenantPaymentRepository tenantPaymentRepository;
    private final TenantInfoRepository tenantInfoRepository;
    private final TenantPaymentValidator tenantPaymentValidator;
    private final OwnerPaymentValidator ownerPaymentValidator;

    @Transactional
    @Override
    public void savePayment(TenantPaymentRequestDto dto) {
        logger.info("[savePayment] Called with dto: {}", dto);
        tenantPaymentValidator.validatePaymentRequest(dto);
        TenantInfoEntity tenant = tenantInfoRepository.findById(dto.getTenantId()).orElse(null);
        tenantPaymentValidator.validateTenantExists(tenant, dto.getTenantId());
        TenantPaymentEntity payment = new TenantPaymentEntity();
        tenant.setId(dto.getTenantId());
        payment.setTenant(tenant);
        payment.setMonth(dto.getMonth());
        payment.setYear(dto.getYear());
        payment.setAmountPaid(BigDecimal.valueOf(dto.getAmount()));
        BedOccupancyEntity activeOccupancy = tenant.getBedOccupancies()
                .stream().filter(t -> t.getStatus().equalsIgnoreCase(BedOccupancyStatus.ACTIVE.name()))
                .findFirst().orElse(null);
        tenantPaymentValidator.validateActiveOccupancy(activeOccupancy, dto.getTenantId());
        assert activeOccupancy != null;
        BigDecimal calculatedAmount = getTenantPayableAmount(activeOccupancy);
        tenantPaymentValidator.validateAmountNotExceeding(BigDecimal.valueOf(dto.getAmount()), calculatedAmount);
        logger.info("[savePayment] Calculated amount for tenant {} is {}", dto.getTenantId(), calculatedAmount);
        payment.setAmount(calculatedAmount);
        payment.setPaymentDate(LocalDateTime.now());
        if (dto.getMode() == PaymentMode.ONLINE) {
            payment.setMode(PaymentMode.ONLINE);
            payment.setTransactionRef(dto.getTransactionRef());
        } else {
            payment.setMode(PaymentMode.CASH);
            payment.setTransactionRef(null);
        }
        payment.setStatus(PaymentStatus.PENDING); // Default to PENDING on save
        tenantPaymentRepository.save(payment);
    }

    /**
     * Retrieves all rent payments for a specific tenant, ordered by year and month descending.
     *
     * @param tenantId the ID of the tenant whose payments are to be fetched
     * @return a list of TenantRentPaymentsDto containing payment details for each month
     */
    @Override
    public List<TenantRentPaymentsDto> getPaymentsByTenant(Long tenantId) {
        logger.info("[getPaymentsByTenant] Called with tenantId: {}", tenantId);
        return tenantPaymentRepository.findByTenantIdOrderByYearDescMonthDesc(tenantId)
                .stream()
                .map(payment -> new TenantRentPaymentsDto(
                        payment.getMonth(),
                        payment.getYear(),
                        payment.getAmount(),
                        payment.getStatus()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public TenantPaymentEntity getPaymentById(Long id) {
        logger.info("[getPaymentById] Called with id: {}", id);
        Optional<TenantPaymentEntity> paymentOpt = tenantPaymentRepository.findById(id);
        return paymentOpt.orElse(null);
    }

    /**
     * Update the status of a payment.
     */
    @Override
    public TenantPaymentEntity updatePaymentStatus(Long paymentId, PaymentStatus status) {
        logger.info("[updatePaymentStatus] Called with paymentId: {}, status: {}", paymentId, status);
        TenantPaymentEntity payment = tenantPaymentRepository.findById(paymentId).orElse(null);
        tenantPaymentValidator.validatePaymentExists(payment, paymentId);
        if (payment != null) {
            logger.info("[updatePaymentStatus] Updating payment id {} to status {}", paymentId, status);
            payment.setStatus(status);
        }
        // TODO: Notify tenant about status update
        assert payment != null;
        return tenantPaymentRepository.save(payment);
    }

    /**
     * Mark a payment as paid by the owner for a specific tenant, month, and year.
     */
    @Override
    @Transactional
    public void markPaymentAsPaidByOwner(Long tenantId, int month, int year) {
        logger.info("[markPaymentAsPaidByOwner] Called with tenantId={}, month={}, year={}", tenantId, month, year);
        TenantInfoEntity tenant = tenantInfoRepository.findById(tenantId).orElse(null);
        tenantPaymentValidator.validateTenantExists(tenant, tenantId);
        assert tenant != null;
        BedOccupancyEntity activeOccupancy = tenant.getBedOccupancies().stream()
                .filter(t -> BedOccupancyStatus.ACTIVE.name().equalsIgnoreCase(t.getStatus()))
                .findFirst()
                .orElse(null);
        tenantPaymentValidator.validateActiveOccupancy(activeOccupancy, tenantId);
        TenantPaymentEntity payment = tenantPaymentRepository.findByTenantIdOrderByYearDescMonthDesc(tenantId).stream()
                .filter(p -> p.getMonth() == month && p.getYear() == year)
                .findFirst().orElse(null);
        if (payment == null) {
            logger.info("[markPaymentAsPaidByOwner] No existing payment found. Creating new payment for tenantId={}", tenantId);
            TenantInfoEntity tenantInfoEntity = tenantInfoRepository.findById(tenantId)
                    .orElseThrow(() -> {
                        logger.error("[markPaymentAsPaidByOwner] Tenant not found with id: {}", tenantId);
                        return new IllegalArgumentException("Tenant not found with id: " + tenantId);
                    });
            // Find active bed occupancy
            BedOccupancyEntity activeOccupancyEntity = tenantInfoEntity.getBedOccupancies().stream()
                    .filter(t -> BedOccupancyStatus.ACTIVE.name().equalsIgnoreCase(t.getStatus()))
                    .findFirst()
                    .orElseThrow(() -> {
                        return new IllegalArgumentException("No active bed occupancy found for tenant id: " + tenantId);
                    });
            // Calculate payable amount
            BigDecimal amountToPay = getTenantPayableAmount(activeOccupancyEntity);
            logger.info("[markPaymentAsPaidByOwner] Calculated amount to pay for tenant {} is {}", tenantId, amountToPay);
            ownerPaymentValidator.validateRentalOptionExists(amountToPay, tenantId);
            TenantPaymentEntity newPayment = new TenantPaymentEntity();
            newPayment.setTenant(tenantInfoEntity);
            newPayment.setMonth(month);
            newPayment.setYear(year);
            newPayment.setAmount(amountToPay);
            newPayment.setAmountPaid(amountToPay);
            newPayment.setMode(PaymentMode.CASH);
            newPayment.setPaymentDate(LocalDateTime.now());
            newPayment.setStatus(PaymentStatus.PAID);
            // Prorate rent if occupancy starts in the middle of the month
            LocalDate occupancyStart = activeOccupancyEntity.getJoiningDate();
            YearMonth paymentMonth = YearMonth.of(year, month);
            BigDecimal proratedAmount = amountToPay;
            if (occupancyStart != null &&
                    (occupancyStart.getYear() == year && occupancyStart.getMonthValue() == month) &&
                    occupancyStart.getDayOfMonth() > 1) {
                assert amountToPay != null;
                proratedAmount = calculateProratedRent(amountToPay, occupancyStart, paymentMonth);
                logger.info("[markPaymentAsPaidByOwner] Prorated amount for tenant {} is {} (start date: {})", tenantId, proratedAmount, occupancyStart);
            }
            newPayment.setAmount(proratedAmount);
            newPayment.setAmountPaid(proratedAmount);
            tenantPaymentRepository.save(newPayment);
            logger.info("[markPaymentAsPaidByOwner] New payment created and saved for tenantId={}", tenantId);
        } else {
            logger.info("[markPaymentAsPaidByOwner] Existing payment found. Marking as PAID for tenantId={}", tenantId);
            payment.setStatus(PaymentStatus.PAID);
            payment.setAmountPaid(payment.getAmount());
            tenantPaymentRepository.save(payment);
            logger.info("[markPaymentAsPaidByOwner] Payment updated and saved for tenantId={}", tenantId);
        }
    }

    @Override
    public List<TenantRentsResponseDto> getTenantRentsSummary(Long tenantId) {
        logger.info("[getTenantRentsSummary] Called with tenantId: {}", tenantId);
        TenantInfoEntity tenant = tenantInfoRepository.findById(tenantId).orElse(null);
        tenantPaymentValidator.validateTenantExists(tenant, tenantId);
        assert tenant != null;
        BedOccupancyEntity activeOccupancy = tenant.getBedOccupancies().stream()
                .filter(t -> BedOccupancyStatus.ACTIVE.name().equalsIgnoreCase(t.getStatus()))
                .findFirst()
                .orElse(null);
        tenantPaymentValidator.validateActiveOccupancy(activeOccupancy, tenantId);
        assert activeOccupancy != null;
        LocalDate joiningDate = activeOccupancy.getJoiningDate();
        YearMonth startMonth = YearMonth.from(joiningDate);
        YearMonth currentMonth = YearMonth.now();
        List<TenantPaymentEntity> payments = tenantPaymentRepository.findByTenantIdOrderByYearDescMonthDesc(tenantId);
        java.util.Map<YearMonth, TenantPaymentEntity> paymentMap = payments.stream()
                .collect(Collectors.toMap(
                        p -> YearMonth.of(p.getYear(), p.getMonth()),
                        p -> p,
                        (existing, replacement) -> existing // keep the first if duplicate
                ));
        List<TenantRentsResponseDto> summary = new java.util.ArrayList<>();
        YearMonth iterMonth = startMonth;
        while (!iterMonth.isAfter(currentMonth)) {
            TenantPaymentEntity payment = paymentMap.get(iterMonth);
            BigDecimal expectedAmount = getTenantPayableAmount(activeOccupancy);
            // Use prorated rent for the initial month if joining is not the 1st
            if (iterMonth.equals(startMonth) && joiningDate.getDayOfMonth() > 1) {
                assert expectedAmount != null;
                expectedAmount = calculateProratedRent(expectedAmount, joiningDate, startMonth);
            }
            if (payment != null) {
                summary.add(new TenantRentsResponseDto(
                        payment.getMonth(),
                        payment.getYear(),
                        payment.getAmount(),
                        payment.getAmountPaid(),
                        payment.getStatus().name()
                ));
            } else {
                summary.add(new TenantRentsResponseDto(
                        iterMonth.getMonthValue(),
                        iterMonth.getYear(),
                        expectedAmount,
                        BigDecimal.ZERO,
                        "UNPAID"
                ));
            }
            iterMonth = iterMonth.plusMonths(1);
        }
        return summary;
    }

    /**
     * Calculate the payable amount for a tenant based on their occupancy.
     */
    private BigDecimal getTenantPayableAmount(BedOccupancyEntity occupancy) {
        logger.debug("[getTenantPayableAmount] Called for occupancy: {}", occupancy != null ? occupancy.getId() : null);
        BedEntity bed = occupancy.getBed();
        if (bed == null) {
            logger.warn("[getTenantPayableAmount] Bed is null for occupancy: {}", occupancy.getId());
            return null;
        }
        RoomEntity room = bed.getRoom();
        if (room == null) {
            logger.warn("[getTenantPayableAmount] Room is null for bed: {}", bed.getId());
            return null;
        }
        String sharingType = occupancy.getSharingType();
        String rentalType = occupancy.getRentalType();
        BigDecimal price = room.getRentalOptions().stream()
                .filter(opt -> opt.getSharingType().equalsIgnoreCase(sharingType)
                        && (rentalType == null || opt.getDurationType().name().equalsIgnoreCase(rentalType)))
                .map(RentalOptionEntity::getPrice)
                .findFirst()
                .orElse(null);
        if (price == null) {
            logger.warn("[getTenantPayableAmount] No rental option found for room: {}, sharingType: {}, rentalType: {}", room.getId(), sharingType, rentalType);
        }
        return price;
    }

    /**
     * Calculate prorated rent for partial month occupancy.
     */
    private BigDecimal calculateProratedRent(BigDecimal monthlyRent, LocalDate occupancyStart, YearMonth month) {
        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();
        LocalDate effectiveStart = occupancyStart.isAfter(monthStart) ? occupancyStart : monthStart;
        int daysInMonth = month.lengthOfMonth();
        int daysStayed = monthEnd.getDayOfMonth() - effectiveStart.getDayOfMonth() + 1;
        BigDecimal perDayRent = monthlyRent.divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);
        return perDayRent.multiply(BigDecimal.valueOf(daysStayed));
    }
}
