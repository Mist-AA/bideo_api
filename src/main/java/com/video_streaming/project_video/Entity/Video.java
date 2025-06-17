package com.video_streaming.project_video.Entity;

import java.util.Date;
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
    private String originalVideoPath;
    private String video_url;
    private String video_title;
    private Date video_uploadDate;
    private String encoded720pPath;
    private Long video_views;

    @ManyToOne
    private User video_uploader;
    
}
