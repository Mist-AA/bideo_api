package com.video_streaming.project_video.Entity;

import java.util.Date;

import com.video_streaming.project_video.Enums.VideoStatus;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;
    private String originalVideoPath;
    private String video_title;
    private Date video_uploadDate;
    private String m3u8Url;
    private Long video_views;
    private String video_duration;
    private String thumbnail_url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoStatus videoStatus =  VideoStatus.PENDING;
    
    @ManyToOne
    @JoinColumn(name = "video_uploader_user_id", referencedColumnName = "userId")
    private User video_uploader;
}
