package com.video_streaming.project_video.Test;

import com.video_streaming.project_video.CloudResConfig.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveMessage(String message) {
        System.out.println("Received: " + message);
    }
}