package com.video_streaming.project_video.Configurations;

import java.util.List;

public class SupportVariablesConfig {  
    public static final List<String> WHITELISTED_PATHS = List.of(
            "/app/login",
            "/app/createUser",
            "/app/health",
            "/video/view/**",
            "/app/verifyToken"
    );

    public static final String refreshTokenURLSuffix = "https://securetoken.googleapis.com/v1/token?key=";
    
    public static final String signInTokenURLSuffix = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";

    public static final String thumbnailURLDefault = "https://github.com/shadcn.png";

    public static final String serviceAccountJson = "/service-account.json";

    public static final String processedVideosFolderPath = "processed_videos";
    
    public static final int cacheTTLMinutes = 30;
}
