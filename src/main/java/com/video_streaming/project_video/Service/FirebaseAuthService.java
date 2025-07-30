package com.video_streaming.project_video.Service;

import org.springframework.http.ResponseEntity;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.video_streaming.project_video.Entity.FirebaseRefreshTokenResponse;
import com.video_streaming.project_video.Entity.FirebaseSignInResponse;

public interface FirebaseAuthService {
    
    /**
     * Verifies the Firebase ID Token received from the client-side.
     *
     * @param idToken The Firebase ID Token to verify.
     * @return A FirebaseToken object if the token is valid.
     * @throws FirebaseAuthException If the token is invalid or verification fails.
     */
    FirebaseToken verifyToken(String idToken) throws FirebaseAuthException;

    /**
     * Signs in a user with email and password using Firebase Authentication.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A FirebaseSignInResponse object containing the sign-in result.
     */
    FirebaseSignInResponse signInWithEmailAndPassword(String email, String password);

    /**
     * Refreshes the Firebase ID Token using a refresh token.
     *
     * @param refreshToken The refresh token to use for refreshing the ID token.
     * @return A FirebaseRefreshTokenResponse object containing the new ID token and other details.
     */
    FirebaseRefreshTokenResponse refreshIdToken (String refreshToken);

    /**
     * Reset the Firebase Create User Password.
     *
     * @param email send email id for user you want to reset.
     * @return A ResponseEntity with status of reset password request.
     */
    ResponseEntity<String> sendPasswordResetEmail(String email);
}
