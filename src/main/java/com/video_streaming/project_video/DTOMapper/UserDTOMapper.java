package com.video_streaming.project_video.DTOMapper;

import com.video_streaming.project_video.DTOs.UserDTO;
import com.video_streaming.project_video.Entity.User;

public class UserDTOMapper {
        
    public UserDTO convertEntityToDTO(User user) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setUser_name(user.getUser_name());
            userDTO.setUser_email(user.getUser_email());
            userDTO.setAvatar_url(user.getAvatar_url());
            
            return userDTO;
    }

    public User convertDTOToEntity(UserDTO userDTO) {
            User user = new User();
            user.setUserId(userDTO.getUserId());
            user.setUser_name(userDTO.getUser_name());
            user.setUser_email(userDTO.getUser_email());
            user.setAvatar_url(userDTO.getAvatar_url());
            
            return user;
    }

}