package com.video_streaming.project_video.Controller;

import com.video_streaming.project_video.Service.S3Service;
import com.video_streaming.project_video.Service.SenderProcessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private SenderProcessService messageSender;

    /**
     * Endpoint to upload a video file.
     * Validates the file type and uploads it to S3.
     * @param multipartFile The video file to upload
     * @return ResponseEntity with the result of the upload
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile videoFile) {
        if (videoFile.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        File tempFile = null;
        try {
            Tika tika = new Tika();
            String detectedType = tika.detect(videoFile.getBytes());
            if (!detectedType.startsWith("video/")) {
                return ResponseEntity.badRequest().body("File is not a valid video type");
            }
            
            // Create a temp file in the system's temp directory
            tempFile = File.createTempFile("upload-", "-" + videoFile.getOriginalFilename());
            videoFile.transferTo(tempFile);

            String result = s3Service.uploadFile(tempFile);
            
            messageSender.sendVideoPath(result);
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file: " + e.getMessage());
            
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Endpoint to download a file from S3.
     * @param fileName The name of the file to download
     * @return The key of the downloaded file
     */
    @GetMapping("/download/{fileName}")
    public String downloadFile(@PathVariable String fileName) {
        return s3Service.downloadFile(fileName).getKey();
    }

}
