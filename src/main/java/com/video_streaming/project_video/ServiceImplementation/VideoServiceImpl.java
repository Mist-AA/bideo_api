package com.video_streaming.project_video.ServiceImplementation;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.video_streaming.project_video.DTOMapper.VideoDTOMapper;
import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.Entity.Video;
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
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private static final Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);
    private final AmazonS3 amazonS3;
    private final UserService userService;
    private final VideoRepository videoRepository;

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

    public String uploadDirectory(File directory, String fileNamePrefix) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            logger.error("File does not exists or is not a directory");
            return "Error: File is not a directory - " + (directory != null ? directory.getAbsolutePath() : "null");
        }
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            throw new RuntimeException("Directory is empty: " + directory.getAbsolutePath());
        }

        String s3FileURL = null;

        for (File file : files) {
            String filenameString = fileNamePrefix + "/" + file.getName();

            PutObjectRequest request = new PutObjectRequest(bucketName, filenameString, file);
            amazonS3.putObject(request);

            if (file.getName().endsWith(".m3u8")) {
                s3FileURL = amazonS3.getUrl(bucketName, filenameString).toString();
            }
        }

        if (s3FileURL == null) {
            throw new RuntimeException(".m3u8 file not found in directory: " + directory.getAbsolutePath());
        }

        return s3FileURL;
    }

    public Page<VideoDTO> getAllVideos(Pageable pageable) {
        Page<Video> videosPage = videoRepository.findAll(pageable);
        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        return videosPage.map(videoDTOMapper::convertEntityToDTO);
    }

    @Transactional
    public Long uploadVideoMetadata(String result, String videoTitle, String userId) {
        VideoDTO videoDTO = new VideoDTO();
        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        videoDTO.setVideo_title(videoTitle);
        videoDTO.setVideo_uploadDate(new Date(System.currentTimeMillis()));
        videoDTO.setM3u8Url(null);
        videoDTO.setVideo_views(0L);
        videoDTO.setVideo_uploader(userService.getUserById(userId));
        
        Video video = videoDTOMapper.convertDTOToEntity(videoDTO);
        video.setOriginalVideoPath(result);
        Video savedVideo = videoRepository.save(video);
        logger.info("Video metadata uploaded successfully");
        return savedVideo.getVideoId();
    }

    @Transactional
    public void updateVideoEncodedPath(Long videoID, String encodedVideoPath) {
        Video video = videoRepository.getReferenceById(videoID);
        video.setM3u8Url(encodedVideoPath);
        videoRepository.save(video);
    }

    public VideoDTO viewVideo(Long videoID) {
        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        Video video = videoRepository.getReferenceById(videoID);

        return videoDTOMapper.convertEntityToDTO(video);
    }
}
