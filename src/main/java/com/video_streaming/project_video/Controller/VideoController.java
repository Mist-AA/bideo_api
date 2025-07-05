package com.video_streaming.project_video.Controller;

import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.Service.VideoService;
import com.video_streaming.project_video.Service.SenderProcessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private SenderProcessService messageSender;

    /**
     * Endpoint to upload a video file.
     * Validates the file type and uploads it to S3.
     * @param multipartFile The video file to upload
     * @return ResponseEntity with the result of the upload
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadVidResponseEntity(@RequestParam("file") MultipartFile videoFile, 
                                                                                @RequestParam String videoTitle, @RequestParam(required = false) String userId) {
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

            String result = videoService.uploadFile(tempFile);
            Long videoID = videoService.uploadVideoMetadata(result, videoTitle, userId);

            messageSender.sendVideoPath(result, videoID);
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

    @GetMapping("/view/{videoID}")
    public ResponseEntity<Map<String, Object>> getVidResponseEntity(@PathVariable Long videoID) {
        String videoURL = videoService.viewVideo(videoID);
        Map<String, Object> response = new HashMap<>();
        response.put("videoId", videoID);
        response.put("videoUrl", videoURL);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<VideoDTO>> getAllVideosList(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("videoId").descending());
        Page<VideoDTO> videoList = videoService.getAllVideos(pageable);
        return ResponseEntity.ok(videoList);
    }

}