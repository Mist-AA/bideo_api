package com.video_streaming.project_video.Enums;

import java.util.List;

public interface SupportVariables {
    List<String> WHITELISTED_PATHS = List.of(
        "/api/app/health",
        "/api/app/createUser",
        "/api/app/login",
        "/api/app/verifyToken",
        "/api/app/reset",
        "/api/vid/view/**",
        "/api/vid/all"
    );

   List<String> ALLOWED_ORIGINS = List.of(
        "https://bideo.tech", 
        "https://www.bideo.tech"
        // Uncomment the line below for development mode
//        ,"http://localhost:3000"
    );

    String refreshTokenURLSuffix = "https://securetoken.googleapis.com/v1/token?key=";
    
    String signInTokenURLSuffix = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";

    String resetUserURLSuffix = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=";

    String thumbnailURLDefault = "https://github.com/shadcn.png";

    String processedVideosFolderPath = "processed_videos";
    
    int cacheTTLMinutes = 3;
}
