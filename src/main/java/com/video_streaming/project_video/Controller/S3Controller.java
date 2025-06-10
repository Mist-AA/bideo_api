package com.video_streaming.project_video.Controller;

import com.video_streaming.project_video.Service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    // Upload a file to S3
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        File tempFile = null;
        try {
            // Create a temp file in the system's temp directory
            tempFile = File.createTempFile("upload-", "-" + multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);

            // Upload to S3
            String result = s3Service.uploadFile(tempFile);
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file: " + e.getMessage());
        } finally {
            // Clean up the temporary file
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }


    // Download a file from S3
    @GetMapping("/download/{fileName}")
    public String downloadFile(@PathVariable String fileName) {
        return s3Service.downloadFile(fileName).getKey();
    }
}
