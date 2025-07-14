package com.video_streaming.project_video.DTOMapper;

import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.Entity.Video;

public class VideoDTOMapper {

    public VideoDTO convertEntityToDTO(Video video) {
        VideoDTO videoDTO = new VideoDTO();
        videoDTO.setVideoId(video.getVideoId());
        videoDTO.setM3u8Url(video.getM3u8Url());
        videoDTO.setVideo_title(video.getVideo_title());
        videoDTO.setVideo_uploadDate(video.getVideo_uploadDate());
        videoDTO.setVideo_views(video.getVideo_views());
        videoDTO.setVideo_duration(video.getVideo_duration());
        videoDTO.setThumbnail_url(video.getThumbnail_url());

        UserDTOMapper userDTOMapper = new UserDTOMapper();
        videoDTO.setVideo_uploader(userDTOMapper.convertEntityToDTO(video.getVideo_uploader()));
        
        return videoDTO;
    }

    public Video convertDTOToEntity(VideoDTO videoDTO) {
        Video video = new Video();
        video.setVideoId(videoDTO.getVideoId());
        video.setM3u8Url(videoDTO.getM3u8Url());
        video.setVideo_title(videoDTO.getVideo_title());
        video.setVideo_uploadDate(videoDTO.getVideo_uploadDate());
        video.setVideo_views(videoDTO.getVideo_views());
        video.setVideo_duration(videoDTO.getVideo_duration());
        video.setThumbnail_url(videoDTO.getThumbnail_url());

        UserDTOMapper userDTOMapper = new UserDTOMapper();
        video.setVideo_uploader(userDTOMapper.convertDTOToEntity(videoDTO.getVideo_uploader()));
        
        return video;
    }
    
}
