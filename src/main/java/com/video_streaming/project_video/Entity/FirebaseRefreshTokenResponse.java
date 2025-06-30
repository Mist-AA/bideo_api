package com.video_streaming.project_video.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FirebaseRefreshTokenResponse {
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String token_type;
    private String id_token;
    private String user_id;

}