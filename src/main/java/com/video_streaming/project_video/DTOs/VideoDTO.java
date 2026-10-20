package com.video_streaming.project_video.DTOs;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.video_streaming.project_video.Enums.VideoCategory;
import com.video_streaming.project_video.Enums.VideoStatus;
import com.video_streaming.project_video.Enums.VideoVisibility;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long videoId;
    private String video_title;
    private String slug;
    private String description;
    private List<String> tags;
    private VideoCategory category;
    private VideoVisibility visibility;
    private Date video_uploadDate;
    private Long video_views;
    private String m3u8Url;
    private UserDTO video_uploader;
    private String video_duration;
    private String iso_duration;
    private String thumbnail_url;
    private VideoStatus videoStatus;
    private SeoMetadataDTO seo;
}
