package com.video_streaming.project_video.ServiceImplementation;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.video_streaming.project_video.Configurations.RabbitMQConfig;
import com.video_streaming.project_video.DTOs.MessageDTO;
import com.video_streaming.project_video.Service.SenderProcessService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SenderProcessServiceImpl implements SenderProcessService {

    private final AmqpTemplate amqpTemplate;

    public void sendVideoPath(String videoFile, Long videoID) {
        MessageDTO message = new MessageDTO();
        message.setVideoFile(videoFile);
        message.setVideoID(videoID);
        amqpTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
    }
}

