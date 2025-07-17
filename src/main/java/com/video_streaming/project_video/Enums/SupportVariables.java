package com.video_streaming.project_video.Enums;

import java.util.List;

public interface SupportVariables {
    List<String> WHITELISTED_PATHS = List.of(
            "/app/health",
            "/app/createUser",
            "/app/login",
            "/app/verifyToken",
            "/video/view/**",
            "/video/all"
    );

    String refreshTokenURLSuffix = "https://securetoken.googleapis.com/v1/token?key=";
    
    String signInTokenURLSuffix = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";

    String thumbnailURLDefault = "https://github.com/shadcn.png";

    String serviceAccountJson = "/service-account.json";

    String processedVideosFolderPath = "processed_videos";
    
    int cacheTTLMinutes = 30;
}
