package com.video_streaming.project_video.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vid_metadata")
public class Video {
    @Id
    private Long video_id;
    private String video_title;
    private String video_url;
    private String video_uploadDate;
    private String video_originalVideoPath;
    private String video_encoded720pPath;
    private Long video_views;

    @ManyToOne
    private User video_uploader;
    
}
