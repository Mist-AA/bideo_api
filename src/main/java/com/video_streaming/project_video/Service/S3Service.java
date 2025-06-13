package com.video_streaming.project_video.Service;

import com.amazonaws.services.s3.model.S3Object;
import java.io.File;

public interface S3Service {
    String uploadFile(File file);
    S3Object downloadFile(String fileName);
}
