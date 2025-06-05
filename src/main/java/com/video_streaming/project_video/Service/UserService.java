package com.video_streaming.project_video.Service;

import org.springframework.stereotype.Service;

import com.video_streaming.project_video.DTOs.UserDTO;

@Service
public interface UserService {
    public void create(String emailId, String password, String user_name) throws Exception;

    public void updateUser(UserDTO userDTO);
}
