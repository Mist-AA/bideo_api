package com.video_streaming.project_video.DTOs;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoMetadataDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private String canonicalUrl;
    private Map<String, Object> jsonLdVideoObject;
    private Map<String, String> openGraph;
    private Map<String, String> twitterCard;
}
