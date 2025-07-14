package com.video_streaming.project_video.ServiceImplementation;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.video_streaming.project_video.Configurations.SupportVariablesConfig;
import com.video_streaming.project_video.DTOMapper.VideoDTOMapper;
import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.Enums.VideoStatus;
import com.video_streaming.project_video.Repository.VideoRepository;
import com.video_streaming.project_video.Service.UserService;
import com.video_streaming.project_video.Service.VideoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private static final Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);
    private final AmazonS3 amazonS3;
    private final UserService userService;
    private final VideoRepository videoRepository;
    private final RedisTemplate<String, VideoDTO> videoRedisTemplate;

    private static final Duration VIDEO_CACHE_TTL = Duration.ofMinutes(SupportVariablesConfig.cacheTTLMinutes);

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public String uploadFile(File file) {
        if (file == null || !file.exists()) {
            logger.error("File does not exists");
            return "Error: File does not exists - " + (file != null ? file.getAbsolutePath() : "null");
        }

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, file.getName(), file);
            amazonS3.putObject(request);
            return amazonS3.getUrl(bucketName, file.getName()).toString();
        } catch (AmazonServiceException ase) {
            logger.error("Service error", ase);
            return "Service error uploading file: " + ase.getMessage();
        } catch (SdkClientException sce) {
            logger.error("Client error", sce);
            return "Client error uploading file: " + sce.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            return "Unexpected error uploading file: " + e.getMessage();
        }
    }

    public String[] uploadDirectory(File directory, String fileNamePrefix) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            logger.error("File does not exists or is not a directory");
            return new String[]{"Error: File is not a directory - ", (directory != null ? directory.getAbsolutePath() : "null")};
        }
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            throw new RuntimeException("Directory is empty: " + directory.getAbsolutePath());
        }

        String s3FileURL = null;
        String s3ThumbnailURL = null;

        for (File file : files) {
            String filenameString = fileNamePrefix + "/" + file.getName();

            PutObjectRequest request = new PutObjectRequest(bucketName, filenameString, file);
            amazonS3.putObject(request);

            if (file.getName().endsWith(".m3u8")) {
                s3FileURL = amazonS3.getUrl(bucketName, filenameString).toString();
            }

            if (file.getName().endsWith(".webp")) {
                s3ThumbnailURL = amazonS3.getUrl(bucketName, filenameString).toString();
            }
        }

        if (s3FileURL == null) {
            throw new RuntimeException(".m3u8 file not found in directory: " + directory.getAbsolutePath());
        }
        return new String[]{ s3FileURL, s3ThumbnailURL };
    }

    public Page<VideoDTO> getAllVideos(Pageable pageable) {
        Page<Video> videosPage = videoRepository.findAll(pageable);
        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        return videosPage.map(videoDTOMapper::convertEntityToDTO);
    }

    @Transactional
    public Long uploadVideoMetadata(String result, String videoTitle, String userId) throws IOException {
        VideoDTO videoDTO = new VideoDTO();
        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        videoDTO.setVideo_title(videoTitle);
        videoDTO.setVideo_uploadDate(new Date(System.currentTimeMillis()));
        videoDTO.setM3u8Url(null);
        videoDTO.setVideo_views(0L);
        videoDTO.setVideo_uploader(userService.getUserById(userId));
        videoDTO.setVideo_duration(getVideoDuration(result));
        videoDTO.setVideoStatus(VideoStatus.PROCESSING);

        Video video = videoDTOMapper.convertDTOToEntity(videoDTO);
        video.setOriginalVideoPath(result);
        Video savedVideo = videoRepository.save(video);
        logger.info("Video metadata uploaded successfully");
        return savedVideo.getVideoId();
    }

    @Transactional
    public void updateVideoEncodedPath(Long videoID, String encodedVideoPath, String s3ThumbnailURL) {
        Video video = videoRepository.getReferenceById(videoID);
        video.setM3u8Url(encodedVideoPath);
        video.setThumbnail_url(s3ThumbnailURL);
        video.setVideoStatus(VideoStatus.COMPLETED);
        videoRepository.save(video);
    }

    @Override
    public VideoDTO viewVideo(Long videoID) {
        String redisKey = "video:" + videoID;

        VideoDTO cached = videoRedisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            return cached;
        }
        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        Video video = videoRepository.getReferenceById(videoID);

        VideoDTO videoDTO = videoDTOMapper.convertEntityToDTO(video);
        videoRedisTemplate.opsForValue().set(redisKey, videoDTO, VIDEO_CACHE_TTL);
        return videoDTO;
    }

    private String getVideoDuration(String inputPath) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                inputPath
        );

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String durationStr = reader.readLine();

        try {
            double durationInSeconds = Double.parseDouble(durationStr);
            return formatDuration(durationInSeconds);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to parse video duration", e);
        }
    }

    private String formatDuration(double seconds) {
        int hours = (int) seconds / 3600;
        int minutes = ((int) seconds % 3600) / 60;
        int secs = (int) seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}
