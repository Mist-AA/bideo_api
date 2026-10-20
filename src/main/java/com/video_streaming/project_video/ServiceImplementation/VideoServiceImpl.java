package com.video_streaming.project_video.ServiceImplementation;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.video_streaming.project_video.DTOMapper.VideoDTOMapper;
import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.DTOs.VideoMetadataUpdateRequest;
import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.Enums.VideoCategory;
import com.video_streaming.project_video.Enums.VideoStatus;
import com.video_streaming.project_video.Enums.VideoVisibility;
import com.video_streaming.project_video.Repository.VideoRepository;
import com.video_streaming.project_video.Service.UserService;
import com.video_streaming.project_video.Service.VideoService;
import com.video_streaming.project_video.Utils.SeoMetadataBuilder;
import com.video_streaming.project_video.Utils.SlugUtils;

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
import java.util.*;

import static com.video_streaming.project_video.Enums.SupportVariables.cacheTTLMinutesVideo;
import static com.video_streaming.project_video.Enums.SupportVariables.cacheTTLMinutesViews;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private static final Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);
    private final AmazonS3 amazonS3;
    private final UserService userService;
    private final VideoRepository videoRepository;
    private final RedisTemplate<String, VideoDTO> videoRedisTemplate;
    private final RedisTemplate<String, Long> viewsRedisTemplate;

    private static final Duration VIDEO_CACHE_TTL = Duration.ofMinutes(cacheTTLMinutesVideo);
    private static final Duration VIEWS_CACHE_TTL = Duration.ofMinutes(cacheTTLMinutesViews);

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public String uploadFile(File file) {
        if (file == null || !file.exists()) {
            logger.error("File does not exist");
            return "Error: File does not exist - " + (file != null ? file.getAbsolutePath() : "null");
        }

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, file.getName(), file);
            amazonS3.putObject(request);
            return amazonS3.getUrl(bucketName, file.getName()).toString();
        } catch (AmazonServiceException ase) {
            logger.error("Service error uploading file", ase);
            return "Service error uploading file: " + ase.getMessage();
        } catch (SdkClientException sce) {
            logger.error("Client error uploading file", sce);
            return "Client error uploading file: " + sce.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error uploading file", e);
            return "Unexpected error uploading file: " + e.getMessage();
        }
    }

    @Override
    public String[] uploadDirectory(File directory, String fileNamePrefix) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            logger.error("File does not exist or is not a directory");
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

    @Override
    public Page<VideoDTO> getAllVideos(Pageable pageable) {
        Page<Video> videosPage = videoRepository.findByVisibilityAndVideoStatus(
                VideoVisibility.PUBLIC,
                VideoStatus.COMPLETED,
                pageable
        );
        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        return videosPage.map(videoDTOMapper::convertEntityToDTO);
    }

    @Override
    @Transactional
    public Long uploadVideoMetadata(String result, String videoTitle, String userId) throws IOException {
        return uploadVideoMetadata(result, videoTitle, null, Collections.emptyList(), VideoCategory.OTHER, VideoVisibility.PUBLIC, userId);
    }

    @Override
    @Transactional
    public Long uploadVideoMetadata(
            String result,
            String videoTitle,
            String description,
            List<String> tags,
            VideoCategory category,
            VideoVisibility visibility,
            String userId
    ) throws IOException {
        Video video = new Video();
        video.setVideo_title(videoTitle);
        video.setDescription(description);
        video.setTags(tags != null ? new ArrayList<>(tags) : new ArrayList<>());
        video.setCategory(category != null ? category : VideoCategory.OTHER);
        video.setVisibility(visibility != null ? visibility : VideoVisibility.PUBLIC);

        // Generate unique SEO slug
        String slug = SlugUtils.generateSlug(videoTitle);
        while (videoRepository.existsBySlug(slug)) {
            slug = SlugUtils.generateSlug(videoTitle);
        }
        video.setSlug(slug);

        video.setVideo_uploadDate(new Date(System.currentTimeMillis()));
        video.setM3u8Url(null);
        video.setVideo_views(0L);
        video.setVideoStatus(VideoStatus.PROCESSING);
        video.setOriginalVideoPath(result);

        if (userId != null && !userId.isBlank()) {
            var userDTO = userService.getUserById(userId);
            if (userDTO != null) {
                com.video_streaming.project_video.DTOMapper.UserDTOMapper userDTOMapper = new com.video_streaming.project_video.DTOMapper.UserDTOMapper();
                video.setVideo_uploader(userDTOMapper.convertDTOToEntity(userDTO));
            }
        }

        // Parse video durations (human-readable and ISO-8601)
        double durationInSeconds = parseDurationInSeconds(result);
        video.setVideo_duration(formatDuration(durationInSeconds));
        video.setIso_duration(SeoMetadataBuilder.toIsoDuration(durationInSeconds));

        Video savedVideo = videoRepository.save(video);
        logger.info("Video metadata uploaded successfully with ID: {} and Slug: {}", savedVideo.getVideoId(), savedVideo.getSlug());
        return savedVideo.getVideoId();
    }

    @Override
    @Transactional
    public void updateVideoEncodedPath(Long videoID, String encodedVideoPath, String s3ThumbnailURL, boolean flag) {
        Video video = videoRepository.findById(videoID)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + videoID));
        if (flag) {
            video.setM3u8Url(encodedVideoPath);
            video.setThumbnail_url(s3ThumbnailURL);
            video.setVideoStatus(VideoStatus.COMPLETED);
        } else {
            video.setVideoStatus(VideoStatus.FAILED);
        }
        videoRepository.save(video);
        invalidateVideoCache(video);
    }

    @Override
    @Transactional
    public void updateVideoEncodedPath(Long videoID, String encodedVideoPath, String s3ThumbnailURL) {
        updateVideoEncodedPath(videoID, encodedVideoPath, s3ThumbnailURL, true);
    }

    @Override
    public VideoDTO viewVideo(Long videoID) {
        String videoKey = "video:id:" + videoID;
        String pendingViewsKey = "video:pendingViews:" + videoID;

        // Atomically increment pending views in Redis
        viewsRedisTemplate.opsForValue().increment(pendingViewsKey);
        viewsRedisTemplate.expire(pendingViewsKey, VIEWS_CACHE_TTL);

        VideoDTO cached = videoRedisTemplate.opsForValue().get(videoKey);
        if (cached != null) {
            return cached;
        }

        Video video = videoRepository.findById(videoID)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + videoID));

        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        VideoDTO videoDTO = videoDTOMapper.convertEntityToDTO(video);

        // Cache video metadata by both ID and Slug
        videoRedisTemplate.opsForValue().set(videoKey, videoDTO, VIDEO_CACHE_TTL);
        if (video.getSlug() != null) {
            videoRedisTemplate.opsForValue().set("video:slug:" + video.getSlug(), videoDTO, VIDEO_CACHE_TTL);
        }
        return videoDTO;
    }

    @Override
    public VideoDTO viewVideoBySlug(String slug) {
        String slugKey = "video:slug:" + slug;

        VideoDTO cached = videoRedisTemplate.opsForValue().get(slugKey);
        if (cached != null) {
            if (cached.getVideoId() != null) {
                String pendingViewsKey = "video:pendingViews:" + cached.getVideoId();
                viewsRedisTemplate.opsForValue().increment(pendingViewsKey);
                viewsRedisTemplate.expire(pendingViewsKey, VIEWS_CACHE_TTL);
            }
            return cached;
        }

        Video video = videoRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Video not found with slug: " + slug));

        String pendingViewsKey = "video:pendingViews:" + video.getVideoId();
        viewsRedisTemplate.opsForValue().increment(pendingViewsKey);
        viewsRedisTemplate.expire(pendingViewsKey, VIEWS_CACHE_TTL);

        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        VideoDTO videoDTO = videoDTOMapper.convertEntityToDTO(video);

        videoRedisTemplate.opsForValue().set(slugKey, videoDTO, VIDEO_CACHE_TTL);
        videoRedisTemplate.opsForValue().set("video:id:" + video.getVideoId(), videoDTO, VIDEO_CACHE_TTL);
        return videoDTO;
    }

    @Override
    @Transactional
    public VideoDTO updateVideoMetadata(Long videoId, String userId, VideoMetadataUpdateRequest updateRequest) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + videoId));

        if (video.getVideo_uploader() == null || !video.getVideo_uploader().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Only the video creator can update this video");
        }

        video.updateMetadata(updateRequest);

        Video updatedVideo = videoRepository.save(video);
        invalidateVideoCache(updatedVideo);

        VideoDTOMapper mapper = new VideoDTOMapper();
        return mapper.convertEntityToDTO(updatedVideo);
    }

    @Override
    public Page<VideoDTO> searchVideos(String query, VideoCategory category, String tag, Pageable pageable) {
        String searchQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        String searchTag = (tag != null && !tag.trim().isEmpty()) ? tag.trim().toLowerCase() : null;

        Page<Video> page = videoRepository.searchVideos(
                searchQuery,
                category,
                searchTag,
                VideoVisibility.PUBLIC,
                VideoStatus.COMPLETED,
                pageable
        );

        VideoDTOMapper mapper = new VideoDTOMapper();
        return page.map(mapper::convertEntityToDTO);
    }

    @Override
    public Page<VideoDTO> getVideosByUserId(String userId, Pageable pageable) {
        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        return videoRepository.findByUploaderId(userId, pageable)
                .map(videoDTOMapper::convertEntityToDTO);
    }

    @Override
    @Transactional
    public boolean deleteVideo(String userId, Long videoID) {
        return videoRepository.findById(videoID)
                .map(video -> {
                    if (video.getVideo_uploader() == null || !video.getVideo_uploader().getUserId().equals(userId)) {
                        logger.warn("User {} is not authorized to delete video {}", userId, videoID);
                        return false;
                    }

                    deleteVideoFromS3(video);
                    invalidateVideoCache(video);
                    videoRepository.delete(video);
                    logger.info("Video {} successfully deleted from database and S3", videoID);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public void syncPendingViewsToDatabase() {
        Set<String> keys = viewsRedisTemplate.keys("video:pendingViews:*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            try {
                String videoIdStr = key.replace("video:pendingViews:", "");
                Long videoId = Long.parseLong(videoIdStr);
                Long pendingViews = viewsRedisTemplate.opsForValue().get(key);

                if (pendingViews != null && pendingViews > 0) {
                    viewsRedisTemplate.delete(key);
                    videoRepository.incrementViews(videoId, pendingViews);
                    logger.debug("Flushed {} pending views for video ID: {}", pendingViews, videoId);

                    // Update cached DTO if currently present in cache
                    String cacheKey = "video:id:" + videoId;
                    VideoDTO cached = videoRedisTemplate.opsForValue().get(cacheKey);
                    if (cached != null) {
                        cached.setVideo_views((cached.getVideo_views() != null ? cached.getVideo_views() : 0L) + pendingViews);
                        videoRedisTemplate.opsForValue().set(cacheKey, cached, VIDEO_CACHE_TTL);
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to sync views for key {}: {}", key, e.getMessage());
            }
        }
    }

    private void invalidateVideoCache(Video video) {
        if (video == null) return;
        if (video.getVideoId() != null) {
            videoRedisTemplate.delete("video:id:" + video.getVideoId());
            videoRedisTemplate.delete("video:" + video.getVideoId()); // legacy fallback
        }
        if (video.getSlug() != null) {
            videoRedisTemplate.delete("video:slug:" + video.getSlug());
        }
    }

    private void deleteVideoFromS3(Video video) {
        // 1. Delete original uploaded video file
        if (video.getOriginalVideoPath() != null) {
            try {
                AmazonS3URI originalUri = new AmazonS3URI(video.getOriginalVideoPath());
                amazonS3.deleteObject(originalUri.getBucket(), originalUri.getKey());
                logger.info("Deleted original S3 file: {}", originalUri.getKey());
            } catch (Exception e) {
                logger.error("Failed to delete original video from S3: {}", e.getMessage());
            }
        }

        // 2. Delete HLS chunks directory and playlist
        if (video.getM3u8Url() != null) {
            try {
                AmazonS3URI hlsUri = new AmazonS3URI(video.getM3u8Url());
                String key = hlsUri.getKey();
                int lastSlashIndex = key.lastIndexOf('/');
                if (lastSlashIndex > 0) {
                    String prefix = key.substring(0, lastSlashIndex + 1);
                    deleteS3DirectoryByPrefix(hlsUri.getBucket(), prefix);
                } else {
                    amazonS3.deleteObject(hlsUri.getBucket(), key);
                }
            } catch (Exception e) {
                logger.error("Failed to delete HLS folder from S3: {}", e.getMessage());
            }
        }

        // 3. Delete thumbnail if separate
        if (video.getThumbnail_url() != null) {
            try {
                AmazonS3URI thumbUri = new AmazonS3URI(video.getThumbnail_url());
                amazonS3.deleteObject(thumbUri.getBucket(), thumbUri.getKey());
            } catch (Exception ignored) {
            }
        }
    }

    private void deleteS3DirectoryByPrefix(String bucket, String prefix) {
        ListObjectsV2Request listRequest = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(prefix);
        ListObjectsV2Result result;
        do {
            result = amazonS3.listObjectsV2(listRequest);
            for (S3ObjectSummary summary : result.getObjectSummaries()) {
                amazonS3.deleteObject(bucket, summary.getKey());
                logger.debug("Deleted S3 chunk: {}", summary.getKey());
            }
            listRequest.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());
        logger.info("Completed S3 directory deletion for prefix: {}", prefix);
    }

    private double parseDurationInSeconds(String inputPath) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                inputPath
        );

        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String durationStr = reader.readLine();
            if (durationStr != null && !durationStr.isBlank()) {
                return Double.parseDouble(durationStr.trim());
            }
        } catch (Exception e) {
            logger.warn("Could not parse ffprobe duration for {}: {}", inputPath, e.getMessage());
        }
        return 0.0;
    }

    private String formatDuration(double seconds) {
        int hours = (int) seconds / 3600;
        int minutes = ((int) seconds % 3600) / 60;
        int secs = (int) seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}
