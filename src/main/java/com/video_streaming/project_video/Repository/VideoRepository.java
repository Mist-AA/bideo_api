package com.video_streaming.project_video.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.Enums.VideoCategory;
import com.video_streaming.project_video.Enums.VideoStatus;
import com.video_streaming.project_video.Enums.VideoVisibility;

public interface VideoRepository extends JpaRepository<Video, Long>, JpaSpecificationExecutor<Video> {

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

    @Query(value = "SELECT DISTINCT v.* FROM vid_metadata v " +
                   "LEFT JOIN video_tags t ON v.video_id = t.video_id " +
                   "WHERE (v.visibility = 'PUBLIC' OR v.visibility IS NULL) " +
                   "AND (v.video_status = 'COMPLETED' OR v.video_status IS NULL) " +
                   "AND (:category IS NULL OR v.category = :category) " +
                   "AND (:tag IS NULL OR LOWER(t.tag) = LOWER(:tag)) " +
                   "AND ( " +
                   "     v.video_title ILIKE CONCAT('%', :query, '%') " +
                   "  OR v.description ILIKE CONCAT('%', :query, '%') " +
                   "  OR v.slug ILIKE CONCAT('%', :query, '%') " +
                   "  OR t.tag ILIKE CONCAT('%', :query, '%') " +
                   "  OR similarity(v.video_title, :query) > 0.2 " +
                   "  OR similarity(v.description, :query) > 0.15 " +
                   ") " +
                   "ORDER BY similarity(v.video_title, :query) DESC, v.video_id DESC",
           countQuery = "SELECT COUNT(DISTINCT v.video_id) FROM vid_metadata v " +
                        "LEFT JOIN video_tags t ON v.video_id = t.video_id " +
                        "WHERE (v.visibility = 'PUBLIC' OR v.visibility IS NULL) " +
                        "AND (v.video_status = 'COMPLETED' OR v.video_status IS NULL) " +
                        "AND (:category IS NULL OR v.category = :category) " +
                        "AND (:tag IS NULL OR LOWER(t.tag) = LOWER(:tag)) " +
                        "AND ( " +
                        "     v.video_title ILIKE CONCAT('%', :query, '%') " +
                        "  OR v.description ILIKE CONCAT('%', :query, '%') " +
                        "  OR v.slug ILIKE CONCAT('%', :query, '%') " +
                        "  OR t.tag ILIKE CONCAT('%', :query, '%') " +
                        "  OR similarity(v.video_title, :query) > 0.2 " +
                        "  OR similarity(v.description, :query) > 0.15 " +
                        ")",
           nativeQuery = true)
    Page<Video> searchVideosFuzzy(
            @Param("query") String query,
            @Param("category") String category,
            @Param("tag") String tag,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Video v SET v.video_views = v.video_views + :views WHERE v.videoId = :videoId")
    int incrementViews(@Param("videoId") Long videoId, @Param("views") Long views);
}