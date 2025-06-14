package com.video_streaming.project_video.ServiceImplementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        System.out.println("🎥 Received video for processing: " + inputPath);

        try {
            String originalFileName = Paths.get(inputPath).getFileName().toString();
            String baseName = originalFileName.replaceAll("\\.\\w+$", "");
            File outputDir = new File("processed_videos");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File tempFile = new File(outputDir, baseName + "_720p_" + System.currentTimeMillis() + ".mp4");

            ProcessBuilder builder = new ProcessBuilder(
                    "ffmpeg", "-i", inputPath,
                    "-vf", "scale=720:480",
                    tempFile.getAbsolutePath());

            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // do nothing
                }
            }

            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("FFmpeg timeout");
            } else {
                System.out.println("FFmpeg process completed successfully.");
            }

            String s3Url = s3Service.uploadFile(tempFile);
            System.out.println("Video uploaded to S3 at: " + s3Url);

            Files.deleteIfExists(tempFile.toPath());

        } catch (Exception e) {
            System.err.println("Video processing/upload failed:");
            e.printStackTrace();
        }
    }

}