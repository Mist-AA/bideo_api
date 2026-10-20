package com.video_streaming.project_video.Scheduler;

import com.video_streaming.project_video.Service.VideoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewSyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ViewSyncScheduler.class);
    private final VideoService videoService;

    // Run every 60 seconds to flush accumulated Redis view counts into PostgreSQL
    @Scheduled(fixedRate = 60000)
    public void syncViews() {
        try {
            videoService.syncPendingViewsToDatabase();
        } catch (Exception e) {
            logger.error("Error occurred while syncing pending views to database: {}", e.getMessage());
        }
    }
}
