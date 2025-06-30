package com.video_streaming.project_video.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.video_streaming.project_video.Entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Video findByOriginalVideoPath(String videoPath);

    @Query("SELECT v.m3u8Url FROM Video v WHERE v.videoId = :videoId")
    String findM3u8UrlByVideoId(Long videoId);
}