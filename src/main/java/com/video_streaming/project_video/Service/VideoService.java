package com.video_streaming.project_video.Service;

import com.amazonaws.services.s3.model.S3Object;
import com.video_streaming.project_video.DTOs.UserDTO;

import java.io.File;

public interface VideoService {
    String uploadFile(File file);
    S3Object downloadFile(String fileName);
    void uploadVideoMetadata(String result, String videoTitle, UserDTO userDTO);
    void updateVideoEncodedPath(String originalFilePath, String encodedVideoPath);
}
