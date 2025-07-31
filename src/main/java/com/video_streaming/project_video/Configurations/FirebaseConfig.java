package com.video_streaming.project_video.Configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.*;


@Configuration
public class FirebaseConfig {

    public static Object firebaseAuth;

    @Value("${service.account.json}")
    private String serviceAccountJson;

    @Getter
    private InputStream serviceAccount;

    @PostConstruct
    public void init() throws Exception {
        if (new File(serviceAccountJson).exists()) {
            this.serviceAccount = new FileInputStream(serviceAccountJson);
        } else {
            this.serviceAccount = new ClassPathResource(serviceAccountJson).getInputStream();
        }
    }

    public FirebaseConfig() throws FileNotFoundException {}

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(getServiceAccount()))
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