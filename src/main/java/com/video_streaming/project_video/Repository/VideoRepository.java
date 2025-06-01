package com.video_streaming.project_video.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.video_streaming.project_video.Entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {}