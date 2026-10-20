package com.video_streaming.project_video.DTOMapper;

import java.util.ArrayList;
import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.Utils.SeoMetadataBuilder;

public class VideoDTOMapper {

    private final SeoMetadataBuilder seoMetadataBuilder = new SeoMetadataBuilder();

    public VideoDTO convertEntityToDTO(Video video) {
        if (video == null) return null;

        VideoDTO videoDTO = new VideoDTO();
        videoDTO.setVideoId(video.getVideoId());
        videoDTO.setM3u8Url(video.getM3u8Url());
        videoDTO.setVideo_title(video.getVideo_title());
        videoDTO.setSlug(video.getSlug());
        videoDTO.setDescription(video.getDescription());
        videoDTO.setTags(video.getTags() != null ? new ArrayList<>(video.getTags()) : new ArrayList<>());
        videoDTO.setCategory(video.getCategory());
        videoDTO.setVisibility(video.getVisibility());
        videoDTO.setVideo_uploadDate(video.getVideo_uploadDate());
        videoDTO.setVideo_views(video.getVideo_views());
        videoDTO.setVideo_duration(video.getVideo_duration());
        videoDTO.setIso_duration(video.getIso_duration());
        videoDTO.setThumbnail_url(video.getThumbnail_url());
        videoDTO.setVideoStatus(video.getVideoStatus());

        if (video.getVideo_uploader() != null) {
            UserDTOMapper userDTOMapper = new UserDTOMapper();
            videoDTO.setVideo_uploader(userDTOMapper.convertEntityToDTO(video.getVideo_uploader()));
        }

        videoDTO.setSeo(seoMetadataBuilder.buildSeoMetadata(video));
        return videoDTO;
    }

    public Video convertDTOToEntity(VideoDTO videoDTO) {
        if (videoDTO == null) return null;

        Video video = new Video();
        video.setVideoId(videoDTO.getVideoId());
        video.setM3u8Url(videoDTO.getM3u8Url());
        video.setVideo_title(videoDTO.getVideo_title());
        video.setSlug(videoDTO.getSlug());
        video.setDescription(videoDTO.getDescription());
        video.setTags(videoDTO.getTags() != null ? new ArrayList<>(videoDTO.getTags()) : new ArrayList<>());
        video.setCategory(videoDTO.getCategory());
        video.setVisibility(videoDTO.getVisibility());
        video.setVideo_uploadDate(videoDTO.getVideo_uploadDate());
        video.setVideo_views(videoDTO.getVideo_views());
        video.setVideo_duration(videoDTO.getVideo_duration());
        video.setIso_duration(videoDTO.getIso_duration());
        video.setThumbnail_url(videoDTO.getThumbnail_url());
        video.setVideoStatus(videoDTO.getVideoStatus());

        if (videoDTO.getVideo_uploader() != null) {
            UserDTOMapper userDTOMapper = new UserDTOMapper();
            video.setVideo_uploader(userDTOMapper.convertDTOToEntity(videoDTO.getVideo_uploader()));
        }
        
        return video;
    }
}
