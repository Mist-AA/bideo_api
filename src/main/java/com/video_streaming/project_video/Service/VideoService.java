package com.video_streaming.project_video.Service;

import com.video_streaming.project_video.DTOs.UserDTO;
import com.video_streaming.project_video.DTOs.VideoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;

public interface VideoService {

    /**
     * Uploads a video file to the server.
     *
     * @param file The video file to be uploaded.
     * @return A string indicating the result of the upload operation.
     */
    public String uploadFile(File file);


    /**
     * Uploads a directory containing video files to the server.
     *
     * @param directory The directory containing video files.
     * @param fileNamePrefix A prefix for the file names.
     * @return A string indicating the result of the upload operation.
     */
    public String uploadDirectory(File directory, String fileNamePrefix);


    /**
     * Retrieves a list of all videos available on the server.
     *
     * @return A list of VideoDTO objects representing the videos.
     */
    public  Page<VideoDTO> getAllVideos(Pageable pageable);


    /**
     * Uploads metadata for a video, including the result of the upload.
     *
     * @param userId The ID of the uploader.
     * @return Video id of the uploaded video.
     */
    public Long uploadVideoMetadata(String result, String videoTitle, String userId);


    /**
     * Updates the encoded video path for a specific video.
     *
     * @param originalFilePath The original file path of the video.
     * @param encodedVideoPath The new encoded video path to be set.
     */
    public void updateVideoEncodedPath(Long videoID, String encodedVideoPath);


    /**
     * Retrieves the URL of a video based on its key suffix.
     *
     * @param videoID The video ID.
     * @return A string representing the URL of the video.
     */
    public String viewVideo(Long videoID);
}
