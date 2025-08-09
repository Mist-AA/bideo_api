package com.video_streaming.project_video.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.video_streaming.project_video.Entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("SELECT v FROM Video v WHERE v.video_uploader.userId = :userId")
    Page<Video> findByUploaderId(@Param("userId") String userId, Pageable pageable);
}