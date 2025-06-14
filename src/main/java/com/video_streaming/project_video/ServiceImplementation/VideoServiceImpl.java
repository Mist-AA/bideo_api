package com.video_streaming.project_video.ServiceImplementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import com.video_streaming.project_video.DTOMapper.VideoDTOMapper;
import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.Repository.VideoRepository;
import com.video_streaming.project_video.Service.VideoService;

import jakarta.validation.Valid;

public class VideoServiceImpl implements VideoService{

    @Autowired
    private VideoRepository videoRepository;

    public String uploadVideoData(@RequestBody @Valid VideoDTO videoDTO) throws Exception {

    VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
    Video video = videoDTOMapper.convertDTOToEntity(videoDTO);
    videoRepository.save(video);

    return "Video uploaded";
    }
}
