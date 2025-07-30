package com.video_streaming.project_video.Controller;

import com.video_streaming.project_video.DTOs.UserDTO;
import com.video_streaming.project_video.Service.UserService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = { "multipart/form-data" })
    public ResponseEntity<String> updateUser(@ModelAttribute UserDTO userDTO) {
        try {
            String response = userService.updateUserProfile(userDTO);
            logger.info(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(500).body("Error updating user: " + e.getMessage());
        }
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") String userId) {
        UserDTO response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
}
