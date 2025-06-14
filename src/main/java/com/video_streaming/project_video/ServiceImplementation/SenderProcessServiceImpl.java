package com.video_streaming.project_video.ServiceImplementation;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.video_streaming.project_video.Configurations.RabbitMQConfig;
import com.video_streaming.project_video.Service.SenderProcessService;

@Service
public class SenderProcessServiceImpl implements SenderProcessService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendVideoPath(String videoFile) {
        amqpTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                videoFile
        );
        System.out.println("Sent: " + videoFile);
    }
}

