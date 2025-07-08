package com.video_streaming.project_video.Controller;

import com.video_streaming.project_video.DTOs.UserDTO;
import com.video_streaming.project_video.Service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "${frontend.exposed.link}")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserDTO userDTO) {
        try {
            String response = userService.updateUserProfile(userDTO);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating user: " + e.getMessage());
        }
    }

    @GetMapping("/getUser")
    public ResponseEntity<UserDTO> getUserById(@RequestParam String user_id) {
            UserDTO response = userService.getUserById(user_id);

            return ResponseEntity.ok(response);
    }
}
