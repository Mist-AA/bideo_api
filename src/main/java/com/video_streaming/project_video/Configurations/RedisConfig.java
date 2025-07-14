package com.video_streaming.project_video.Configurations;

import org.springframework.context.annotation.Configuration;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.video_streaming.project_video.DTOs.VideoDTO;

import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public RedisTemplate<String, VideoDTO> videoRedisTemplate() {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        Jackson2JsonRedisSerializer<VideoDTO> messagePackSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, VideoDTO.class);

        RedisTemplate<String, VideoDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(messagePackSerializer);
        template.afterPropertiesSet();
        return template;
    }
}