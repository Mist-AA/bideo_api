package com.video_streaming.project_video.Test;

import com.video_streaming.project_video.CloudResConfig.RabbitMQConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(String message) {
        amqpTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
        System.out.println("Sent: " + message);
    }
}

