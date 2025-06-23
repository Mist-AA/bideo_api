package com.video_streaming.project_video.ServiceImplementation;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.video_streaming.project_video.DTOMapper.VideoDTOMapper;
import com.video_streaming.project_video.DTOs.UserDTO;
import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.Repository.VideoRepository;
import com.video_streaming.project_video.Service.VideoService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl implements VideoService {

    private final AmazonS3 amazonS3;
    @Autowired
    private VideoRepository videoRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public VideoServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String uploadFile(File file) {
        if (file == null || !file.exists()) {
            return "Error: File does not exist - " + (file != null ? file.getAbsolutePath() : "null");
        }

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, file.getName(), file);
            amazonS3.putObject(request);
            return amazonS3.getUrl(bucketName, file.getName()).toString();
        } catch (AmazonServiceException ase) {
            // To do: Add logging here
            // Fix global exception handling
            ase.printStackTrace();
            return "Service error uploading file: " + ase.getMessage();
        } catch (SdkClientException sce) {
            // To do: Add logging here
            sce.printStackTrace();
            return "Client error uploading file: " + sce.getMessage();
        } catch (Exception e) {
            // To do: Add logging here
            e.printStackTrace();
            return "Unexpected error uploading file: " + e.getMessage();
        }
    }

    public String uploadDirectory(File directory, String fileNamePrefix) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
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

    public List<VideoDTO> getAllVideos() {
        List<Video> videos = videoRepository.findAll();
        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        return videos.stream()
                .map(videoDTOMapper::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void uploadVideoMetadata(String result, String videoTitle, UserDTO userDTO) {
        VideoDTO videoDTO = new VideoDTO();
        VideoDTOMapper videoDTOMapper = new VideoDTOMapper();
        videoDTO.setVideo_url(null);
        videoDTO.setVideo_title(videoTitle);
        videoDTO.setVideo_uploadDate(new Date(System.currentTimeMillis()));
        videoDTO.setVideo_views(0L);
        
        // Testing code
        UserDTO userDTO2 = new UserDTO();
        userDTO2.setUser_id("9nu1abAxD0ReFFN79I6rnfcsDe22");
        userDTO2.setUser_name("online_db");
        userDTO2.setUser_email("test6@ok.con");
        videoDTO.setVideo_uploader(userDTO2);
        // End of testing code
        
        Video video = videoDTOMapper.convertDTOToEntity(videoDTO);
        video.setOriginalVideoPath(result);
        video.setEncoded720pPath(null);
        videoRepository.save(video);
    }

    @Transactional
    public void updateVideoEncodedPath(String originalFilePath, String encodedVideoPath) {
        Video video = videoRepository.findByOriginalVideoPath(originalFilePath);
        if (video != null) {
            video.setEncoded720pPath(encodedVideoPath);
            videoRepository.save(video);
        }
    }

    public String viewVideo(String videoKeySuffix) {
        String videoURL = videoRepository.findEncoded720PathByOriginalVideoPath(videoKeySuffix);
        if (videoURL == null) {
            throw new RuntimeException("Video not found");
        }
        return videoURL;
    }
}
