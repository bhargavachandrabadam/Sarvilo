package com.easy.stazy.shared.scheduled;

import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.pg.enums.BedOccupancyStatus;
import com.easy.stazy.pgmanagement.pg.repository.BedOccupancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BedOccupancyStatusScheduler {
    private final BedOccupancyRepository bedOccupancyRepository;

    // Runs every day at 1:00 AM
    @Scheduled(cron = "0 0 1 * * *")
    public void updateVacatedStatuses() {
        List<BedOccupancyEntity> activeOccupancies = bedOccupancyRepository.findByStatus(BedOccupancyStatus.ACTIVE.name());
        LocalDate today = LocalDate.now();
        for (BedOccupancyEntity occupancy : activeOccupancies) {
            if (occupancy.getExitDate() != null && !occupancy.getExitDate().isAfter(today)) {
                occupancy.setStatus(BedOccupancyStatus.VACATED.name());
                log.info("Updated BedOccupancy {} to VACATED (exit date: {})", occupancy.getId(), occupancy.getExitDate());
            }
        }
        bedOccupancyRepository.saveAll(activeOccupancies);
    }
}

