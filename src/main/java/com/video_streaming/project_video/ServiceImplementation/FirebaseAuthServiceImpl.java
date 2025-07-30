package com.video_streaming.project_video.ServiceImplementation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.video_streaming.project_video.Entity.FirebaseRefreshTokenRequest;
import com.video_streaming.project_video.Entity.FirebaseRefreshTokenResponse;
import com.video_streaming.project_video.Entity.FirebaseSignInRequest;
import com.video_streaming.project_video.Entity.FirebaseSignInResponse;
import com.video_streaming.project_video.Service.FirebaseAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.stereotype.Service;

import static com.video_streaming.project_video.Enums.SupportVariables.refreshTokenURLSuffix;
import static com.video_streaming.project_video.Enums.SupportVariables.signInTokenURLSuffix;
import static com.video_streaming.project_video.Enums.SupportVariables.resetUserURLSuffix;

import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseAuthServiceImpl implements FirebaseAuthService {

    @Value("${firebase.api.key}")
    private String FIREBASE_API_KEY;
    private final RestTemplate restTemplate = new RestTemplate();

    private String getSignInUrl() {
        return signInTokenURLSuffix + FIREBASE_API_KEY;
    }

    private String getRefreshTokenURL() {
        return refreshTokenURLSuffix + FIREBASE_API_KEY;
    }

    private String getResetUserURL() {
        return resetUserURLSuffix + FIREBASE_API_KEY;
    }

    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public FirebaseSignInResponse signInWithEmailAndPassword(String email, String password) {
        FirebaseSignInRequest request = new FirebaseSignInRequest(email, password, true);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FirebaseSignInRequest> entity = new HttpEntity<>(request, headers);

        try {

            ResponseEntity<FirebaseSignInResponse> response = restTemplate
                    .postForEntity(getSignInUrl(), entity, FirebaseSignInResponse.class);

            return response.getBody();

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Firebase sign-in failed" + e.getResponseBodyAsString());
        }
    }

    public FirebaseRefreshTokenResponse refreshIdToken(String refreshToken) {
        String refreshUrl = getRefreshTokenURL();

        FirebaseRefreshTokenRequest request = new FirebaseRefreshTokenRequest(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<FirebaseRefreshTokenRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<FirebaseRefreshTokenResponse> response = restTemplate
                    .postForEntity(refreshUrl, entity, FirebaseRefreshTokenResponse.class);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Token refresh failed: " + e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<String> sendPasswordResetEmail(String email) {
        String url = getResetUserURL();

        Map<String, Object> payload = new HashMap<>();
        payload.put("requestType", "PASSWORD_RESET");
        payload.put("email", email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            return restTemplate.postForEntity(url, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to send password reset email: " + e.getResponseBodyAsString());
        }
    }

}