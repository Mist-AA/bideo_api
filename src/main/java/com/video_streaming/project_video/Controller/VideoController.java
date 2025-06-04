package com.video_streaming.project_video.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.DTOMapper.VideoDTOMapper;
import com.video_streaming.project_video.Repository.VideoRepository;

import com.video_streaming.project_video.Firebase.FirebaseVideoUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private FirebaseVideoUploadService videoUploadService;

//    @PostMapping("/upload")
//    public String uploadVideo(@RequestBody @Valid VideoDTO videoDTO) throws Exception {
//
//        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
//        Video video = videoDTOMapper.convertDTOToEntity(videoDTO);
//        videoRepository.save(video);
//
//        return "Video uploaded";
//    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            String videoUrl = videoUploadService.uploadVideo(file);
            return ResponseEntity.ok("Video uploaded successfully: " + videoUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}

