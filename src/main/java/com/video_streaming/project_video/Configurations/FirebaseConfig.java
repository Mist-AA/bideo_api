package com.video_streaming.project_video.Configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.video_streaming.project_video.Enums.SupportVariables.serviceAccountJson;

@Configuration
public class FirebaseConfig {

    public static Object firebaseAuth;
    private final InputStream serviceAccount = new FileInputStream(serviceAccountJson);

    public FirebaseConfig() throws FileNotFoundException {}

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            return FirebaseApp.initializeApp(firebaseOptions);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
}

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}