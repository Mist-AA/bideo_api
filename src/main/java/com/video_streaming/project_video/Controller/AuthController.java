package com.video_streaming.project_video.Controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import com.video_streaming.project_video.Entity.FirebaseRefreshTokenResponse;
import com.video_streaming.project_video.Entity.FirebaseSignInResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.video_streaming.project_video.Service.FirebaseAuthService;
import com.video_streaming.project_video.Service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final FirebaseAuthService firebaseAuthService;
    private final UserService userService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is running!");
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestParam String email, @RequestParam String password,
                                             @RequestParam String user_name, @RequestParam String avatar_url) {
        try {
            userService.create(email, password, user_name, avatar_url);
            logger.info("Successfully created user");
            return ResponseEntity.ok("User created successfully!");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(400).body("Error creating user: " + e.getMessage());
        }
    }

    @PostMapping("/verifyToken")
    public ResponseEntity<Map<String, String>> verifyToken(@RequestBody String idToken) {
        try {
            FirebaseToken decodedToken = firebaseAuthService.verifyToken(idToken);
            String uid = decodedToken.getUid();

            Map<String, String> response = new HashMap<>();
            response.put("id", uid);

            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            logger.error(e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid token: " + e.getMessage());
            return ResponseEntity.status(401).body(error);
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