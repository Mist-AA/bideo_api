package com.video_streaming.project_video.DTOs;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoDTO {
    private Long videoId;
    private String video_title;
    private String video_url;
    private Date video_uploadDate;
    private Long video_views;
    private String m3u8Url;
    private UserDTO video_uploader;
    
}
