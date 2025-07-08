package com.video_streaming.project_video.Service;

public interface SenderProcessService {
    
    /**
     * This method is used to send a video file path to the sender.
     * 
     * @param videoFile The path to the video file that needs to be sent.
     * @param videoID The ID of the video associated with the file.
     */
    void sendVideoPath(String videoFile, Long videoID);
}
