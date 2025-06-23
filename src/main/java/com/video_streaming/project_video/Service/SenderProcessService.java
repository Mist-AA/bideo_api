package com.video_streaming.project_video.Service;

public interface SenderProcessService {
    
    /**
     * This method is used to send a video file path to the sender.
     * 
     * @param videoFile The path to the video file that needs to be sent.
     */
    public void sendVideoPath(String videoFile);
}
