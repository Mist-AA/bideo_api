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
    private Long user_id;
    private String user_name;
    private String user_email;
    private String user_password;
    private String user_role;
}