package com.video_streaming.project_video.Controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.video_streaming.project_video.CloudResConfig.FirebaseAuthService;
import com.video_streaming.project_video.Service.UserService;

@RestController
@RequestMapping("")
public class AuthController {

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @Autowired
    private UserService userService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is running!");
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestParam String email, @RequestParam String password, @RequestParam String user_name) {
        try {
            userService.create(email, password, user_name);
            return ResponseEntity.ok("User created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error creating user: " + e.getMessage());
        }
    }

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