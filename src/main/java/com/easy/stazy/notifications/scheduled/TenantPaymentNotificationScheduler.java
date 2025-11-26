package com.easy.stazy.notifications.scheduled;

import com.easy.stazy.payments.enums.PaymentStatus;
import com.easy.stazy.payments.entity.TenantPaymentEntity;
import com.easy.stazy.payments.repository.TenantPaymentRepository;
import com.easy.stazy.pgmanagement.pg.enums.UserType;
import com.easy.stazy.notifications.service.FcmNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.pg.repository.BedOccupancyRepository;
import com.easy.stazy.pgmanagement.tenant.repository.TenantInfoRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantPaymentNotificationScheduler {
    private final TenantPaymentRepository tenantPaymentRepository;
    private final BedOccupancyRepository bedOccupancyRepository;
    private final TenantInfoRepository tenantInfoRepository;
    private final FcmNotificationService fcmNotificationService;

    // Runs every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void sendUnpaidTenantNotifications() {
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        if (dayOfMonth < 1 || dayOfMonth > 10) {
            return; // Only run for the first 10 days
        }
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        List<BedOccupancyEntity> activeOccupancies = bedOccupancyRepository.findByStatus("ACTIVE");
        // Collect all tenant and user IDs
        List<Long> tenantIds = activeOccupancies.stream()
                .map(o -> o.getTenant().getId())
                .toList();
        List<Long> userIds = activeOccupancies.stream()
                .map(o -> o.getTenant().getUser().getId())
                .toList();
        // Fetch all paid payments for these tenants for this month
        List<TenantPaymentEntity> paidPayments = tenantPaymentRepository.findByTenant_IdInAndMonthAndYearAndStatus(
                tenantIds, month, year, PaymentStatus.PAID
        );
        Set<Long> paidTenantIds = paidPayments.stream()
                .map(p -> p.getTenant().getId())
                .collect(java.util.stream.Collectors.toSet());
        // Notify only unpaid tenants
        for (BedOccupancyEntity occupancy : activeOccupancies) {
            Long tenantId = occupancy.getTenant().getId();
            Long userId = occupancy.getTenant().getUser().getId();
            if (!paidTenantIds.contains(tenantId)) {
                Map<String, String> payLoadMap = Map.of(
                        "type", "RENT_REMINDER",
                        "month", String.valueOf(month),
                        "year", String.valueOf(year),
                        "tenantId", String.valueOf(tenantId)
                );
                fcmNotificationService.sendNotificationToUser(
                        userId,
                        UserType.TENANT,
                        "Unpaid Rent",
                        "Your rent for this month is unpaid. Please pay as soon as possible.",
                        payLoadMap
                );
                log.info("Push notification sent to unpaid tenant user: {} for month: {}-{}", userId, month, year);
            }
        }
    }
}
