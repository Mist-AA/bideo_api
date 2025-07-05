package com.video_streaming.project_video.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_info")
public class User {
    @Id
    private String userId;
    private String user_name;
    private String user_email;
    private String user_role;
    private String thumbnail_url;
}