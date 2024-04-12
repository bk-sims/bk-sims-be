package com.dalv.bksims.tasks;

import com.dalv.bksims.models.entities.social_points_management.Activity;
import com.dalv.bksims.models.enums.Status;
import com.dalv.bksims.models.repositories.social_points_management.ActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ActivityStatusUpdater {
    @Autowired
    private ActivityRepository activityRepo;

    @Scheduled(cron = "0 0 0 * * *") // Runs every day at 00:00 AM
    public void updateActivityStatuses() {
        log.info("Updating activity statuses...");
        List<String> targetStatuses = List.of(Status.OPEN.toString(), Status.PENDING.toString());
        List<Activity> overdueActivities = activityRepo.findByStatusInAndEndDateBefore(
                targetStatuses,
                LocalDateTime.now().toString()
        );
        for (Activity activity : overdueActivities) {
            activity.setStatus(Status.CLOSED.toString());
            activityRepo.save(activity);
        }
    }
}
