package com.video_streaming.project_video.CloudResConfig;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        // Verify the Firebase ID Token received from the client-side
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }
}