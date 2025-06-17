package com.video_streaming.project_video.DTOs;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoDTO {
    private String video_title;
    private String video_url;
    private Date video_uploadDate;
    private Long video_views;
    private UserDTO video_uploader;
    
}
