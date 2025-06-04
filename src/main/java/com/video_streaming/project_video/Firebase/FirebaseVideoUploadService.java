package com.video_streaming.project_video.Firebase;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseVideoUploadService {

    public String uploadVideo(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create("videos/" + fileName, file.getBytes(), file.getContentType());

        // Generate a signed URL (optional)
        return "https://storage.googleapis.com/" + bucket.getName() + "/videos/" + fileName;
    }
}

