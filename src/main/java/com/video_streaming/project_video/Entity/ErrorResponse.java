package com.video_streaming.project_video.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {

    private String message;
    private String userId;

    public ErrorResponse(String message) {
        this.message = message;
    }

}
