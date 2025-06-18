package com.video_streaming.project_video.Service;

import com.video_streaming.project_video.DTOs.UserDTO;

import java.io.File;

public interface VideoService {
    String uploadFile(File file);
    String uploadDirectory(File directory, String fileNamePrefix);
    void uploadVideoMetadata(String result, String videoTitle, UserDTO userDTO);
    void updateVideoEncodedPath(String originalFilePath, String encodedVideoPath);
    String viewVideo(String videoKeySuffix);
}
