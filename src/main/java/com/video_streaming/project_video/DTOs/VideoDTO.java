package com.video_streaming.project_video.DTOs;

import java.util.Date;

import com.video_streaming.project_video.Enums.VideoStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoDTO {
    private Long videoId;
    private String video_title;
    private Date video_uploadDate;
    private Long video_views;
    private String m3u8Url;
    private UserDTO video_uploader;
    private String video_duration;
    private String thumbnail_url;
    private VideoStatus videoStatus;
}
