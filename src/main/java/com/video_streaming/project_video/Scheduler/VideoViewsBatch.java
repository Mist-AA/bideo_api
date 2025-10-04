package com.video_streaming.project_video.Scheduler;

import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.Repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class VideoViewsBatch {

    private static final Logger logger = LoggerFactory.getLogger(VideoViewsBatch.class);
    private final StringRedisTemplate viewsRedisTemplate;
    private final VideoRepository videoRepository;

    @Scheduled(fixedRate = 2, timeUnit = TimeUnit.HOURS)
    public void flushPendingViewsToDB() {
        Set<String> keys = viewsRedisTemplate.keys("video:*:pendingViews");

        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            try {
                Long pendingViews = Long.valueOf(Objects.requireNonNull(viewsRedisTemplate.opsForValue().get(key)));
                if (pendingViews > 0) {
                    Long videoID = Long.parseLong(key.split(":")[1]);
                    Video video = videoRepository.findById(videoID).orElse(null);
                    if (video != null) {
                        video.setVideo_views(video.getVideo_views() + pendingViews);
                        videoRepository.save(video);
                    }
                    viewsRedisTemplate.delete(key);
                }
                logger.info("Batch job ran successfully at : {}", DateTime.now());
            } catch (Exception e) {
                logger.error("Video views update failed {}", e.getMessage());
            }
        }
    }
}
