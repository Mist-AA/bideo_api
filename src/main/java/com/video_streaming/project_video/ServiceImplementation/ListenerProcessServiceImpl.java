package com.video_streaming.project_video.ServiceImplementation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.video_streaming.project_video.Configurations.RabbitMQConfig;
import com.video_streaming.project_video.Service.ListenerProcessService;
import com.video_streaming.project_video.Service.S3Service;

@Service
public class ListenerProcessServiceImpl implements ListenerProcessService {

    @Autowired
    private S3Service s3Service;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveVideo(String inputPath) {

         try {
            System.out.println("Received video for processing: " + inputPath);

            String outputPath = inputPath.replace(".mp4", "_720p.mp4");

            ProcessBuilder builder = new ProcessBuilder(
                    "ffmpeg", "-i", inputPath,
                    "-vf", "scale=720:480",
                    outputPath
            );
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // Code for debugging ffmpeg output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // do nothing
            }
            
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("FFmpeg timeout");
            } else {
                System.out.println("FFmpeg process completed successfully.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        } 
    }
}