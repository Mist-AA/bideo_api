package com.video_streaming.project_video.Service;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.video_streaming.project_video.Entity.FirebaseRefreshTokenResponse;
import com.video_streaming.project_video.Entity.FirebaseSignInResponse;

public interface FirebaseAuthService {
    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException;
    public FirebaseSignInResponse signInWithEmailAndPassword(String email, String password);
    public FirebaseRefreshTokenResponse refreshIdToken (String refreshToken);
}
