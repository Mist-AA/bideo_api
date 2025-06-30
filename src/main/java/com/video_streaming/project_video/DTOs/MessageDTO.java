package com.video_streaming.project_video.DTOs;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO implements Serializable {
    private String videoFile;
    private Long videoID;
    
}
