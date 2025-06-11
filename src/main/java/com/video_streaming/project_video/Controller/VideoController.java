package com.video_streaming.project_video.Controller;

import com.video_streaming.project_video.DTOMapper.VideoDTOMapper;
import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.Repository.VideoRepository;
import com.video_streaming.project_video.Service.S3Service;

import jakarta.validation.Valid;

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
    private VideoRepository videoRepository;

    // Upload a file to S3
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        File tempFile = null;
        try {
            Tika tika = new Tika();
            String detectedType = tika.detect(multipartFile.getBytes());
            if (!detectedType.startsWith("video/")) {
                return ResponseEntity.badRequest().body("File is not a valid video type");
            }
            
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

    public String uploadVideoData(@RequestBody @Valid VideoDTO videoDTO) throws Exception {

        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        Video video = videoDTOMapper.convertDTOToEntity(videoDTO);
        videoRepository.save(video);

        return "Video uploaded";
    }
}
