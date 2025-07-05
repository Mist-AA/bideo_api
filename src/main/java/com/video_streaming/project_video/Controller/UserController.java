package com.video_streaming.project_video.Controller;

import com.video_streaming.project_video.DTOMapper.UserDTOMapper;
import com.video_streaming.project_video.DTOs.UserDTO;
import com.video_streaming.project_video.Entity.ErrorResponse;
import com.video_streaming.project_video.Entity.User;
import com.video_streaming.project_video.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestParam String user_id,
            @RequestParam String user_name,
            @RequestParam String user_email,
            @RequestParam String thumbnail_url) {
        try {
            User user = userRepository.findByUserId(user_id);

            if (user == null) {
                return ResponseEntity.status(404).body("User not found with ID: " + user_id);
            }

            if (!user_name.equals(user.getUser_name())) {
                user.setUser_name(user_name);
            }
            if (!user_email.equals(user.getUser_email())) {
                user.setUser_email(user_email);
            }
            if (!thumbnail_url.equals(user.getThumbnail_url())) {
                user.setThumbnail_url(thumbnail_url);
            }

            userRepository.save(user);

            return ResponseEntity.ok(new ErrorResponse("User updated successfully"));
        } catch (Exception e) {
            ErrorResponse err = new ErrorResponse("Error updating user: " + e.getMessage());
            err.setUserId(user_id);
            return ResponseEntity.status(400).body(err);
        }
    }

    @GetMapping("/getUser")
    public ResponseEntity<?> getUserById(@RequestParam String user_id) {
        try {
            User user = userRepository.findByUserId(user_id);

            if (user == null) {
                ErrorResponse err = new ErrorResponse("User not found with ID: " + user_id);
                err.setUserId(user_id);
                return ResponseEntity.status(404).body(err);
            }

            UserDTOMapper mapper = new UserDTOMapper();
            UserDTO dto = mapper.convertEntityToDTO(user);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            ErrorResponse err = new ErrorResponse("Error retrieving user: " + e.getMessage());
            err.setUserId(user_id);
            return ResponseEntity.status(500).body(err);
        }
    }

}
