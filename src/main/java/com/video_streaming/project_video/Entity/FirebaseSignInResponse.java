package com.video_streaming.project_video.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FirebaseSignInResponse {
    private String idToken;
    private String refreshToken;

}
