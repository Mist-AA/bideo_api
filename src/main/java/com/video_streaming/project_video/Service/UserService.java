package com.video_streaming.project_video.Service;

import com.video_streaming.project_video.DTOs.UserDTO;

public interface UserService {
    public void create(String emailId, String password, String user_name) throws Exception;
}
