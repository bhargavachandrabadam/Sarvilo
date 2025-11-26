package com.easy.stazy.notifications.scheduled;

import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.pg.enums.UserType;
import com.easy.stazy.pgmanagement.pg.repository.BedOccupancyRepository;
import com.easy.stazy.notifications.service.FcmNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Scheduler to send push notifications to tenants one day before their joining date.
 */
@Component
@RequiredArgsConstructor
public class TenantJoiningNotificationScheduler {
    private final BedOccupancyRepository bedOccupancyRepository;
    private final FcmNotificationService notificationService;



    /**
     * Runs every day at 9 AM. Sends push notifications to tenants whose joining date is tomorrow.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendJoiningNotifications() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<BedOccupancyEntity> occupancies = bedOccupancyRepository.findByJoiningDateAndStatus(tomorrow, "ACTIVE");
        for (BedOccupancyEntity occupancy : occupancies) {
            Long userId = occupancy.getTenant().getUser().getId();
            Map<String,String> dataPayload = Map.of(
                    "tenantName", occupancy.getTenant().getUser().getName(),
                    "pgName", occupancy.getTenant().getPg().getPgName(),
                    "joiningDate", occupancy.getJoiningDate().toString());

            notificationService.sendNotificationAndLog(userId, UserType.TENANT,
                    "Upcoming Stay Reminder",
                    "Dear " + occupancy.getTenant().getUser().getName() + ", your stay at " + occupancy.getTenant().getPg().getPgName() +
                            " begins tomorrow (" + occupancy.getJoiningDate() + "). We look forward to hosting you!",
                    dataPayload);
        }
    }
}
