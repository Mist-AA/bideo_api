package com.video_streaming.project_video.Controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import com.video_streaming.project_video.Entity.FirebaseRefreshTokenResponse;
import com.video_streaming.project_video.Entity.FirebaseSignInResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.video_streaming.project_video.Service.FirebaseAuthService;
import com.video_streaming.project_video.Service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app")
public class AuthController {

    private final FirebaseAuthService firebaseAuthService;
    private final UserService userService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is running!");
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestParam String email, @RequestParam String password, @RequestParam String user_name, @RequestParam String thumbnail_url) {
        try {
            userService.create(email, password, user_name, thumbnail_url);
            return ResponseEntity.ok("User created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error creating user: " + e.getMessage());
        }
    }

    @PostMapping("/verifyToken")
    public ResponseEntity<String> verifyToken(@RequestBody String idToken) {
        try {
            FirebaseToken decodedToken = firebaseAuthService.verifyToken(idToken);
            String uid = decodedToken.getUid();
            return ResponseEntity.ok("Token verified successfully! UID: " + uid);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<FirebaseSignInResponse> login(@RequestParam String email,@RequestParam String password) {
        FirebaseSignInResponse response = firebaseAuthService.signInWithEmailAndPassword(email, password);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<FirebaseRefreshTokenResponse> refreshToken(@RequestParam String refreshToken) {
        FirebaseRefreshTokenResponse response = firebaseAuthService.refreshIdToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}