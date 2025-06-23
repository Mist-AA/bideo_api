package com.video_streaming.project_video.Service;

public interface ListenerProcessService {
    
    /**
     * This method is used to receive a video file from the listener.
     * 
     * @param videoFile The path to the video file that needs to be processed.
     */
    public void receiveVideo(String videoFile);
}
