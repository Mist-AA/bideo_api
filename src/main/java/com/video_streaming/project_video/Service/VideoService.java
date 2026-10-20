package com.video_streaming.project_video.Service;

import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.DTOs.VideoMetadataUpdateRequest;
import com.video_streaming.project_video.Enums.VideoCategory;
import com.video_streaming.project_video.Enums.VideoVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface VideoService {

    /**
     * Uploads a video file to the server.
     *
     * @param file The video file to be uploaded.
     * @return A string indicating the result of the upload operation.
     */
    String uploadFile(File file);

    /**
     * Uploads a directory containing video files to the server.
     *
     * @param directory The directory containing video files.
     * @param fileNamePrefix A prefix for the file names.
     * @return Strings for m3u8 file and thumbnail url indicating the result of the upload operation.
     */
    String[] uploadDirectory(File directory, String fileNamePrefix);

    /**
     * Retrieves a list of all public completed videos available on the server.
     * @param pageable Information required for pagination.
     * @return A page of VideoDTO objects representing the videos.
     */
    Page<VideoDTO> getAllVideos(Pageable pageable);

    /**
     * Uploads metadata for a video with title and user ID.
     *
     * @param result S3 URL of the original video.
     * @param videoTitle The title of the video.
     * @param userId The ID of the uploader.
     * @return Video id of the uploaded video.
     */
    Long uploadVideoMetadata(String result, String videoTitle, String userId) throws IOException;

    /**
     * Uploads metadata for a video with full SEO fields.
     */
    Long uploadVideoMetadata(
            String result,
            String videoTitle,
            String description,
            List<String> tags,
            VideoCategory category,
            VideoVisibility visibility,
            String userId
    ) throws IOException;

    /**
     * Updates the encoded video path for a specific video with success flag.
     *
     * @param videoID The ID of the video.
     * @param encodedVideoPath The new encoded video path to be set.
     * @param s3ThumbnailURL The path for thumbnail of video to be set.
     * @param flag The flag to check if video upload successful or failed.
     */
    void updateVideoEncodedPath(Long videoID, String encodedVideoPath, String s3ThumbnailURL, boolean flag);

    /**
     * Updates the encoded video path for a specific video (default flag true).
     */
    void updateVideoEncodedPath(Long videoID, String encodedVideoPath, String s3ThumbnailURL);

    /**
     * Retrieves the video information from video ID.
     *
     * @param videoID The ID of the video.
     * @return VideoDTO object for specific video ID.
     */
    VideoDTO viewVideo(Long videoID);

    /**
     * Retrieves the video information from SEO slug.
     *
     * @param slug The SEO slug of the video.
     * @return VideoDTO object.
     */
    VideoDTO viewVideoBySlug(String slug);

    /**
     * Updates video metadata (title, description, tags, category, visibility).
     */
    VideoDTO updateVideoMetadata(Long videoId, String userId, VideoMetadataUpdateRequest updateRequest);

    /**
     * Searches videos by keyword, category, and tag with pagination.
     */
    Page<VideoDTO> searchVideos(String query, VideoCategory category, String tag, Pageable pageable);

    /**
     * Retrieves videos uploaded by current user.
     *
     * @param userId The ID of the uploader.
     * @param pageable The parameters of pagination.
     * @return VideoDTO for all user uploaded videos.
     */
    Page<VideoDTO> getVideosByUserId(String userId, Pageable pageable);

    /**
     * Deletes the video relevant for specific video ID, including S3 cleanup.
     * @param userId The ID of the uploader.
     * @param videoID The ID of the video.
     */
    boolean deleteVideo(String userId, Long videoID);

    /**
     * Flushes accumulated pending views from Redis to PostgreSQL.
     */
    void syncPendingViewsToDatabase();
}
