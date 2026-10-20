package com.video_streaming.project_video.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.Enums.VideoCategory;
import com.video_streaming.project_video.Enums.VideoStatus;
import com.video_streaming.project_video.Enums.VideoVisibility;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT v FROM Video v WHERE v.video_uploader.userId = :userId")
    Page<Video> findByUploaderId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT v FROM Video v WHERE v.video_uploader.userId = :userId AND v.visibility = :visibility")
    Page<Video> findByUploaderIdAndVisibility(
            @Param("userId") String userId,
            @Param("visibility") VideoVisibility visibility,
            Pageable pageable
    );

    Optional<Video> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT v FROM Video v WHERE (v.visibility = :visibility OR v.visibility IS NULL) AND (v.videoStatus = :videoStatus OR v.videoStatus IS NULL)")
    Page<Video> findByVisibilityAndVideoStatus(
            @Param("visibility") VideoVisibility visibility,
            @Param("videoStatus") VideoStatus videoStatus,
            Pageable pageable
    );

    @Query("SELECT DISTINCT v FROM Video v " +
           "LEFT JOIN v.tags t " +
           "WHERE v.visibility = :visibility " +
           "AND v.videoStatus = :videoStatus " +
           "AND (:category IS NULL OR v.category = :category) " +
           "AND (:tag IS NULL OR LOWER(t) = LOWER(:tag)) " +
           "AND (:query IS NULL OR :query = '' OR " +
           "     LOWER(v.video_title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "     LOWER(v.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Video> searchVideos(
            @Param("query") String query,
            @Param("category") VideoCategory category,
            @Param("tag") String tag,
            @Param("visibility") VideoVisibility visibility,
            @Param("videoStatus") VideoStatus videoStatus,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Video v SET v.video_views = v.video_views + :views WHERE v.videoId = :videoId")
    int incrementViews(@Param("videoId") Long videoId, @Param("views") Long views);
}