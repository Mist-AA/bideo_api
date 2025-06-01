package com.video_streaming.project_video.DTOMapper;

import com.video_streaming.project_video.DTOs.UserDTO;
import com.video_streaming.project_video.Entity.User;

public class UserDTOMapper {
        
    public UserDTO convertEntityToDTO(User user) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUser_id(user.getUser_id());
            userDTO.setUser_name(user.getUser_name());
            userDTO.setUser_email(user.getUser_email());
            
            return userDTO;
    }

    public User convertDTOToEntity(UserDTO userDTO) {
            User user = new User();
            user.setUser_id(userDTO.getUser_id());
            user.setUser_name(userDTO.getUser_name());
            user.setUser_email(userDTO.getUser_email());
            
            return user;
    }

}