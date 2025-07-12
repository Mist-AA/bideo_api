package com.video_streaming.project_video.Controller;

import com.video_streaming.project_video.DTOs.UserDTO;
import com.video_streaming.project_video.Service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @RequestMapping(value = "/update" , method = RequestMethod.POST, consumes = { "multipart/form-data" })
    public ResponseEntity<String> updateUser(@ModelAttribute UserDTO userDTO) {
        try {
            String response = userService.updateUserProfile(userDTO);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating user: " + e.getMessage());
        }
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") String userId) {
        UserDTO response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

}
