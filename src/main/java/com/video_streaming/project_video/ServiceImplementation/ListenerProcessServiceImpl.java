package com.video_streaming.project_video.ServiceImplementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.video_streaming.project_video.Configurations.RabbitMQConfig;
import com.video_streaming.project_video.DTOs.MessageDTO;
import com.video_streaming.project_video.Service.ListenerProcessService;
import com.video_streaming.project_video.Service.VideoService;

import lombok.RequiredArgsConstructor;

import static com.video_streaming.project_video.Enums.SupportVariables.processedVideosFolderPath;

@Service
@RequiredArgsConstructor
public class ListenerProcessServiceImpl implements ListenerProcessService {

    private static final Logger logger = LoggerFactory.getLogger(ListenerProcessServiceImpl.class);
    private final VideoService videoService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveVideo(MessageDTO message) {
        String inputPath = message.getVideoFile();
        Long videoID = message.getVideoID();
        long timestamp = System.currentTimeMillis();

        try {
            URI uri = new URI(inputPath);
            String fileName = Paths.get(uri.getPath()).getFileName().toString();
            String baseName = fileName.replaceAll("\\.\\w+$", "");

            File outputDir = new File(processedVideosFolderPath);
            if (!outputDir.exists()) outputDir.mkdirs();
            
            File encodedMp4 = new File(outputDir, baseName + "_720p_" + timestamp + ".mp4");
            File hlsDir = new File(outputDir, baseName + "_hls_" + timestamp);
            if (!hlsDir.exists()) hlsDir.mkdirs();

            File outputImagePath = new File(outputDir, baseName + "thumbnail" + ".webp");
            File generatedThumbnail = generateThumbnailFromVideo(inputPath, outputImagePath);
            Path targetPath = Paths.get(hlsDir.getAbsolutePath(), generatedThumbnail.getName());
            Files.move(generatedThumbnail.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            boolean encodingSuccess = encodeTo720p(inputPath, encodedMp4);
            if (!encodingSuccess) throw new RuntimeException("MP4 encoding failed");

            boolean hlsSuccess = convertToHLS(encodedMp4, hlsDir);
            if (!hlsSuccess) throw new RuntimeException("HLS conversion failed");

            String[] s3Info = videoService.uploadDirectory(hlsDir, baseName + "_hls_" + timestamp);
            String s3URL = s3Info[0];
            String s3ThumbnailURL = s3Info[1];
            logger.info("Video uploaded to S3 at: {}", s3URL);
            videoService.updateVideoEncodedPath(videoID, s3URL, s3ThumbnailURL, true);

        } catch (Exception e) {
            videoService.updateVideoEncodedPath(videoID, null, null, false);
            logger.error("Video processing/upload failed:", e);
        }
        finally {
            File outputDir = new File(processedVideosFolderPath);
            if (outputDir.exists() && outputDir.isDirectory()) {
                for (File file : Objects.requireNonNull(outputDir.listFiles())) {
                    deleteRecursively(file);
                }
            }
        }
    }

    private File generateThumbnailFromVideo(String inputVideoPath, File outputImagePath) throws IOException, InterruptedException {
        ProcessBuilder thumbnailBuilder = new ProcessBuilder(
            "ffmpeg",
            "-i", inputVideoPath,
            "-vf", "thumbnail,scale=320:-1",
            "-frames:v", "1",
            outputImagePath.getAbsolutePath()
        );

        processBuilderFFmpeg(thumbnailBuilder);
        return outputImagePath;
    }
    
    private boolean encodeTo720p(String inputPath, File encodedMp4) throws IOException, InterruptedException {
        ProcessBuilder encodeBuilder = new ProcessBuilder(
                "ffmpeg", "-i", inputPath,
                "-vf", "scale=-2:720",
                "-c:v", "libx264",
                "-preset", "fast",
                "-crf", "23",
                "-y",
                encodedMp4.getAbsolutePath()
        );
        return processBuilderFFmpeg(encodeBuilder);
    }

    private boolean convertToHLS(File encodedMp4, File hlsDir) throws IOException, InterruptedException {
        File playlistFile = new File(hlsDir, "output.m3u8");

        ProcessBuilder hlsBuilder = new ProcessBuilder(
                "ffmpeg", "-y",
                "-i", encodedMp4.getAbsolutePath(),
                "-force_key_frames", "expr:gte(t,n_forced*5)",
                "-c:v", "libx264",
                "-preset", "fast",
                "-crf", "23",
                "-start_number", "0",
                "-hls_time", "5",
                "-hls_list_size", "0",
                "-f", "hls",
                playlistFile.getAbsolutePath()
        );
        return processBuilderFFmpeg(hlsBuilder);
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

    private boolean processBuilderFFmpeg(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        processBuilder.redirectErrorStream(true);
        Process hlsProcess = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(hlsProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("[FFmpeg log]: {}", line);
            }
        }

        boolean finished = hlsProcess.waitFor(10, TimeUnit.SECONDS);
        if (!finished) {
            hlsProcess.destroyForcibly();
            throw new RuntimeException("FFmpeg timeout");
        } else {
            return true;
        }
    }
}