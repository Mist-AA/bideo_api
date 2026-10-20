package com.video_streaming.project_video.DTOs;

import java.util.List;
import com.video_streaming.project_video.Enums.VideoCategory;
import com.video_streaming.project_video.Enums.VideoVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoMetadataUpdateRequest {
    private String videoTitle;
    private String description;
    private List<String> tags;
    private VideoCategory category;
    private VideoVisibility visibility;
}
