package com.video_streaming.project_video.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FirebaseSignInRequest {
    // Getters and Setters
    private String email;
    private String password;
    private boolean returnSecureToken;

    public FirebaseSignInRequest(String email, String password, boolean b) {
        setEmail(email);
        setPassword(password);
        setReturnSecureToken(b);
    }
}
