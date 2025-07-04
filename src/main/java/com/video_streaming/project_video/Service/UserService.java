package com.video_streaming.project_video.Service;

import com.video_streaming.project_video.DTOs.UserDTO;

public interface UserService {
    
    /**
     * This method is used to create a new user with the provided email, password, and username.
     * 
     * @param emailId The email ID of the user.
     * @param password The password for the user.
     * @param user_name The username for the user.
     * @throws Exception If there is an error during user creation.
     */
    public void create(String emailId, String password, String user_name, String thumbnail_url) throws Exception;
    public String updateUserProfile(UserDTO userDTO);
    public UserDTO getUserById(String userId);
}
