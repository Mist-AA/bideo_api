package com.video_streaming.project_video.Controller;

import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.DTOs.VideoMetadataUpdateRequest;
import com.video_streaming.project_video.Enums.VideoCategory;
import com.video_streaming.project_video.Enums.VideoVisibility;
import com.video_streaming.project_video.Service.VideoService;

import lombok.RequiredArgsConstructor;

import com.video_streaming.project_video.Service.SenderProcessService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vid")
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);
    private final VideoService videoService;
    private final SenderProcessService messageSender;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVidResponseEntity(
            @RequestParam("file") MultipartFile videoFile, 
            @RequestParam String videoTitle, 
            @RequestParam String userId,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false, defaultValue = "OTHER") VideoCategory category,
            @RequestParam(required = false, defaultValue = "PUBLIC") VideoVisibility visibility) {

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
            tempFile = File.createTempFile("upload-", "-" + Objects.requireNonNull(videoFile.getOriginalFilename()).
                    substring(0, Math.min(videoFile.getOriginalFilename().length(), 10)));
            videoFile.transferTo(tempFile);

            String result = videoService.uploadFile(tempFile);
            Long videoID = videoService.uploadVideoMetadata(
                    result,
                    videoTitle,
                    description,
                    tags,
                    category,
                    visibility,
                    userId
            );

            messageSender.sendVideoPath(result, videoID);
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file: " + e.getMessage());
            
        } finally {
            if (tempFile != null && tempFile.exists()) {
                boolean file_status = tempFile.delete();
                if (!file_status) {
                    logger.error("Temp file not deleted");
                }
            }
        }
    }

    @GetMapping("/view/{videoID}")
    public ResponseEntity<VideoDTO> getVidResponseEntity(@PathVariable Long videoID) {
        VideoDTO videoDTO = videoService.viewVideo(videoID);
        return ResponseEntity.ok(videoDTO);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<VideoDTO> getVideoBySlug(@PathVariable String slug) {
        VideoDTO videoDTO = videoService.viewVideoBySlug(slug);
        return ResponseEntity.ok(videoDTO);
    }

    @PutMapping("/{videoId}")
    public ResponseEntity<VideoDTO> updateVideoMetadata(
            @PathVariable Long videoId,
            @RequestParam String userId,
            @RequestBody VideoMetadataUpdateRequest updateRequest) {
        VideoDTO updatedVideo = videoService.updateVideoMetadata(videoId, userId, updateRequest);
        return ResponseEntity.ok(updatedVideo);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<VideoDTO>> searchVideos(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) VideoCategory category,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("videoId").descending());
        Page<VideoDTO> results = videoService.searchVideos(query, category, tag, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<VideoDTO>> getAllVideosList(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("videoId").descending());
        Page<VideoDTO> videoList = videoService.getAllVideos(pageable);
        return ResponseEntity.ok(videoList);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<VideoDTO>> getUserUploadedVideos(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("videoId").descending());
        Page<VideoDTO> videos = videoService.getVideosByUserId(userId, pageable);
        return ResponseEntity.ok(videos);
    }

    @RequestMapping(value = "/user/{userId}/{videoId}", method = {RequestMethod.POST, RequestMethod.DELETE})
    public ResponseEntity<String> deleteVideoByUser(@PathVariable String userId, @PathVariable Long videoId) {
        boolean response = videoService.deleteVideo(userId, videoId);
        if (response) {
            return ResponseEntity.ok("Successfully deleted video");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete video");
        }
    }

}