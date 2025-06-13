package com.video_streaming.project_video.Service;

import org.springframework.web.bind.annotation.RequestBody;

import com.video_streaming.project_video.DTOs.VideoDTO;

import jakarta.validation.Valid;

public interface VideoService {
    public String uploadVideoData(@RequestBody @Valid VideoDTO videoDTO) throws Exception;
}
