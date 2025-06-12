package com.video_streaming.project_video.Controller;

import com.video_streaming.project_video.Test.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private MessageSender messageSender;

    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
        messageSender.sendMessage(message);
        return "Message sent to RabbitMQ!";
    }
}
