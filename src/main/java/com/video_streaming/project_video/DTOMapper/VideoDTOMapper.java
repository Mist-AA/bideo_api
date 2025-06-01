package com.video_streaming.project_video.DTOMapper;

import com.video_streaming.project_video.DTOs.UserDTO;
import com.video_streaming.project_video.DTOs.VideoDTO;
import com.video_streaming.project_video.Entity.Video;

public class VideoDTOMapper {

    public VideoDTO convertEntityToDTO(Video video) {
        VideoDTO videoDTO = new VideoDTO();
        videoDTO.setVideo_title(video.getVideo_title());
        videoDTO.setVideo_url(video.getVideo_url());
        videoDTO.setVideo_uploadDate(video.getVideo_uploadDate());
        videoDTO.setVideo_views(video.getVideo_views());
        
        UserDTOMapper userDTOMapper = new UserDTOMapper();
        UserDTO userDTO = userDTOMapper.convertEntityToDTO(video.getVideo_uploader());
        videoDTO.setVideo_uploader(userDTO);
        
        return videoDTO;
    }

    public Video convertDTOToEntity(VideoDTO videoDTO) {
        Video video = new Video();
        video.setVideo_title(videoDTO.getVideo_title());
        video.setVideo_url(videoDTO.getVideo_url());
        video.setVideo_uploadDate(videoDTO.getVideo_uploadDate());
        video.setVideo_views(videoDTO.getVideo_views());
        
        UserDTOMapper userDTOMapper = new UserDTOMapper();
        video.setVideo_uploader(userDTOMapper.convertDTOToEntity(videoDTO.getVideo_uploader()));
        
        return video;
    }
    
}
