package com.video_streaming.project_video.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.video_streaming.project_video.DTOs.VideoMetadataUpdateRequest;
import com.video_streaming.project_video.Enums.VideoCategory;
import com.video_streaming.project_video.Enums.VideoStatus;
import com.video_streaming.project_video.Enums.VideoVisibility;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vid_metadata")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;
    private String originalVideoPath;
    private String video_title;

    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "video_tags", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoCategory category = VideoCategory.OTHER;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoVisibility visibility = VideoVisibility.PUBLIC;

    private Date video_uploadDate;
    private String m3u8Url;

    @Builder.Default
    private Long video_views = 0L;

    private String video_duration;
    private String iso_duration;
    private String thumbnail_url;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoStatus videoStatus = VideoStatus.PENDING;
    
    @ManyToOne
    @JoinColumn(name = "video_uploader_user_id", referencedColumnName = "userId")
    private User video_uploader;

    public void updateMetadata(VideoMetadataUpdateRequest request) {
        if (request == null) return;
        if (request.getVideoTitle() != null && !request.getVideoTitle().isBlank()) {
            this.video_title = request.getVideoTitle();
        }
        if (request.getDescription() != null) {
            this.description = request.getDescription();
        }
        if (request.getTags() != null) {
            this.tags = new ArrayList<>(request.getTags());
        }
        if (request.getCategory() != null) {
            this.category = request.getCategory();
        }
        if (request.getVisibility() != null) {
            this.visibility = request.getVisibility();
        }
    }
}
