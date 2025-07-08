package com.video_streaming.project_video.Service;

import com.video_streaming.project_video.DTOs.MessageDTO;

public interface ListenerProcessService {
    
    /**
     * This method is used to receive a video file from the listener.
     * 
     * @param message video file object containing video file path and video ID.
     */
    void receiveVideo(MessageDTO message);
}
