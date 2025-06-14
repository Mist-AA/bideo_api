package com.video_streaming.project_video.ServiceImplementation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.video_streaming.project_video.Service.FirebaseAuthService;

import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthServiceImpl implements FirebaseAuthService {

    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        // Verify the Firebase ID Token received from the client-side
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }
}