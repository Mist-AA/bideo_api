package com.video_streaming.project_video.Service;

import com.video_streaming.project_video.DTOs.VideoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;

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
     * Retrieves a list of all videos available on the server.
     * @param pageable Information required for pagination.
     * @return A list of VideoDTO objects representing the videos.
     */
    Page<VideoDTO> getAllVideos(Pageable pageable);

    /**
     * Uploads metadata for a video, including the result of the upload.
     *
     * @param userId The ID of the uploader.
     * @return Video id of the uploaded video.
     */
    Long uploadVideoMetadata(String result, String videoTitle, String userId) throws IOException;

    /**
     * Updates the encoded video path for a specific video.
     *
     * @param videoID The ID of the video.
     * @param encodedVideoPath The new encoded video path to be set.
     * @param s3ThumbnailURL The path for thumbnail of video to be set.
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
     * Retrieves videos uploaded by current user.
     *
     * @param userId The ID of the uploader.
     * @param pageable The parameters of pagination.
     * @return VideoDTO for all user uploaded videos.
     */
    Page<VideoDTO> getVideosByUserId(String userId, Pageable pageable);

    /**
     * Deletes the video relevant for specific video ID.
     * @param userId The ID of the uploader.
     * @param videoID The ID of the video.
     */
    boolean deleteVideo(String userId, Long videoID);
}
