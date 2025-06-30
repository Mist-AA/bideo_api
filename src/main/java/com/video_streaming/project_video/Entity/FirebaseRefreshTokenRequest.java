package com.video_streaming.project_video.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FirebaseRefreshTokenRequest {
    private String grant_type;
    private String refresh_token;

    public FirebaseRefreshTokenRequest(String refresh_token) {
        this.grant_type = "refresh_token";
        this.refresh_token = refresh_token;
    }

}
