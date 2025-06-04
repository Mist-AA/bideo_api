package com.video_streaming.project_video.Controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.video_streaming.project_video.Firebase.FirebaseAuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @PostMapping("/verify")
    public ResponseEntity<String> verifyToken(@RequestBody String idToken) {
        try {
            FirebaseToken decodedToken = firebaseAuthService.verifyToken(idToken);
            String uid = decodedToken.getUid();  // Firebase User ID
            return ResponseEntity.ok("Token verified successfully! UID: " + uid);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }
    }
}