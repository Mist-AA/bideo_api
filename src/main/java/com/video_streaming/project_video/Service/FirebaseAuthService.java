package com.video_streaming.project_video.Service;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

public interface FirebaseAuthService {
    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException;
}
