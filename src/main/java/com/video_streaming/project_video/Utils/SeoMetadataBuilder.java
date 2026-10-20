package com.video_streaming.project_video.Utils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import com.video_streaming.project_video.DTOs.SeoMetadataDTO;
import com.video_streaming.project_video.Entity.Video;
import org.springframework.stereotype.Component;

@Component
public class SeoMetadataBuilder {

    private static final String BASE_URL = "https://bideo.rajit.cc";

    public SeoMetadataDTO buildSeoMetadata(Video video) {
        if (video == null) {
            return null;
        }

        String title = video.getVideo_title() != null ? video.getVideo_title() : "Untitled Video";
        String description = (video.getDescription() != null && !video.getDescription().isBlank())
                ? video.getDescription()
                : "Watch " + title + " on bideo.rajit.cc";

        String identifier = (video.getSlug() != null && !video.getSlug().isBlank())
                ? video.getSlug()
                : String.valueOf(video.getVideoId());

        String canonicalUrl = BASE_URL + "/watch/" + identifier;
        String embedUrl = BASE_URL + "/embed/" + identifier;
        String thumbnailUrl = video.getThumbnail_url() != null ? video.getThumbnail_url() : BASE_URL + "/default-thumbnail.jpg";
        String uploadDateIso = video.getVideo_uploadDate() != null
                ? Instant.ofEpochMilli(video.getVideo_uploadDate().getTime()).toString()
                : Instant.now().toString();

        String isoDuration = video.getIso_duration() != null ? video.getIso_duration() : "PT0S";

        // JSON-LD VideoObject Schema.org
        Map<String, Object> jsonLd = new LinkedHashMap<>();
        jsonLd.put("@context", "https://schema.org");
        jsonLd.put("@type", "VideoObject");
        jsonLd.put("name", title);
        jsonLd.put("description", description);
        jsonLd.put("thumbnailUrl", List.of(thumbnailUrl));
        jsonLd.put("uploadDate", uploadDateIso);
        jsonLd.put("duration", isoDuration);
        if (video.getM3u8Url() != null) {
            jsonLd.put("contentUrl", video.getM3u8Url());
        }
        jsonLd.put("embedUrl", embedUrl);
        if (video.getCategory() != null) {
            jsonLd.put("genre", video.getCategory().name());
        }
        if (video.getTags() != null && !video.getTags().isEmpty()) {
            jsonLd.put("keywords", String.join(", ", video.getTags()));
        }

        Map<String, Object> interactionStatistic = new LinkedHashMap<>();
        interactionStatistic.put("@type", "InteractionCounter");
        interactionStatistic.put("interactionType", Map.of("@type", "WatchAction"));
        interactionStatistic.put("userInteractionCount", video.getVideo_views() != null ? video.getVideo_views() : 0L);
        jsonLd.put("interactionStatistic", interactionStatistic);

        if (video.getVideo_uploader() != null) {
            Map<String, Object> author = new LinkedHashMap<>();
            author.put("@type", "Person");
            author.put("name", video.getVideo_uploader().getUser_name() != null ? video.getVideo_uploader().getUser_name() : "Creator");
            jsonLd.put("author", author);
        }

        // Open Graph
        String truncatedDescription = description.length() > 200 ? description.substring(0, 197) + "..." : description;
        Map<String, String> openGraph = new LinkedHashMap<>();
        openGraph.put("og:site_name", "bideo.rajit.cc");
        openGraph.put("og:type", "video.other");
        openGraph.put("og:title", title);
        openGraph.put("og:description", truncatedDescription);
        openGraph.put("og:image", thumbnailUrl);
        openGraph.put("og:url", canonicalUrl);
        if (video.getM3u8Url() != null) {
            openGraph.put("og:video", video.getM3u8Url());
            openGraph.put("og:video:type", "application/x-mpegURL");
        }

        // Twitter Card
        Map<String, String> twitterCard = new LinkedHashMap<>();
        twitterCard.put("twitter:card", "player");
        twitterCard.put("twitter:site", "@bideo");
        twitterCard.put("twitter:title", title);
        twitterCard.put("twitter:description", truncatedDescription);
        twitterCard.put("twitter:image", thumbnailUrl);
        twitterCard.put("twitter:player", embedUrl);
        twitterCard.put("twitter:player:width", "1280");
        twitterCard.put("twitter:player:height", "720");

        return SeoMetadataDTO.builder()
                .title(title)
                .description(description)
                .canonicalUrl(canonicalUrl)
                .jsonLdVideoObject(jsonLd)
                .openGraph(openGraph)
                .twitterCard(twitterCard)
                .build();
    }

    public static String toIsoDuration(double seconds) {
        if (seconds <= 0) {
            return "PT0S";
        }
        return Duration.ofSeconds(Math.round(seconds)).toString();
    }
}
