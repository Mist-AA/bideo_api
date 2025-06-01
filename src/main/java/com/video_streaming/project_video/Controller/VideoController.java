package com.video_streaming.project_video.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.DTOMapper.VideoDTOMapper;
import com.video_streaming.project_video.Repository.VideoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    @PostMapping("/upload")
    public String uploadVideo(@RequestBody @Valid VideoDTO videoDTO) throws Exception {

        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        Video video = videoDTOMapper.convertDTOToEntity(videoDTO);
        videoRepository.save(video);

        return "Video uploaded";
    }
}

