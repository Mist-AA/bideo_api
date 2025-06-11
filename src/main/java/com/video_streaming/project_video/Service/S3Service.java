package com.video_streaming.project_video.Service;

import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public interface S3Service {
    String uploadFile(File file);
    S3Object downloadFile(String fileName);
}
