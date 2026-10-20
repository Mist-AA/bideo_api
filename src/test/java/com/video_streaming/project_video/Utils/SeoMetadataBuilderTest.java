package com.video_streaming.project_video.Utils;

import com.video_streaming.project_video.DTOs.SeoMetadataDTO;
import com.video_streaming.project_video.Entity.User;
import com.video_streaming.project_video.Entity.Video;
import com.video_streaming.project_video.Enums.VideoCategory;
import com.video_streaming.project_video.Enums.VideoStatus;
import com.video_streaming.project_video.Enums.VideoVisibility;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SeoMetadataBuilderTest {

    private final SeoMetadataBuilder seoMetadataBuilder = new SeoMetadataBuilder();

    @Test
    void testBuildSeoMetadataComplete() {
        User uploader = new User();
        uploader.setUserId("user-123");
        uploader.setUser_name("Alice Dev");

        Video video = Video.builder()
                .videoId(42L)
                .video_title("Spring Boot SEO Masterclass")
                .slug("spring-boot-seo-masterclass-a1b2")
                .description("A comprehensive guide to backend SEO and metadata.")
                .tags(List.of("java", "springboot", "seo"))
                .category(VideoCategory.TECH)
                .visibility(VideoVisibility.PUBLIC)
                .video_uploadDate(new Date(1700000000000L))
                .m3u8Url("https://s3.amazonaws.com/bucket/hls/output.m3u8")
                .thumbnail_url("https://s3.amazonaws.com/bucket/hls/thumbnail.webp")
                .video_duration("00:05:30")
                .iso_duration("PT5M30S")
                .video_views(1250L)
                .videoStatus(VideoStatus.COMPLETED)
                .video_uploader(uploader)
                .build();

        SeoMetadataDTO seo = seoMetadataBuilder.buildSeoMetadata(video);

        assertNotNull(seo);
        assertEquals("Spring Boot SEO Masterclass", seo.getTitle());
        assertEquals("A comprehensive guide to backend SEO and metadata.", seo.getDescription());
        assertEquals("https://bideo.rajit.cc/watch/spring-boot-seo-masterclass-a1b2", seo.getCanonicalUrl());

        // Check JSON-LD
        Map<String, Object> jsonLd = seo.getJsonLdVideoObject();
        assertNotNull(jsonLd);
        assertEquals("https://schema.org", jsonLd.get("@context"));
        assertEquals("VideoObject", jsonLd.get("@type"));
        assertEquals("Spring Boot SEO Masterclass", jsonLd.get("name"));
        assertEquals("PT5M30S", jsonLd.get("duration"));
        assertEquals("https://s3.amazonaws.com/bucket/hls/output.m3u8", jsonLd.get("contentUrl"));
        assertEquals("https://bideo.rajit.cc/embed/spring-boot-seo-masterclass-a1b2", jsonLd.get("embedUrl"));
        assertEquals("TECH", jsonLd.get("genre"));
        assertEquals("java, springboot, seo", jsonLd.get("keywords"));

        // Check OpenGraph
        Map<String, String> og = seo.getOpenGraph();
        assertNotNull(og);
        assertEquals("bideo.rajit.cc", og.get("og:site_name"));
        assertEquals("video.other", og.get("og:type"));
        assertEquals("https://s3.amazonaws.com/bucket/hls/thumbnail.webp", og.get("og:image"));

        // Check Twitter
        Map<String, String> tw = seo.getTwitterCard();
        assertNotNull(tw);
        assertEquals("player", tw.get("twitter:card"));
        assertEquals("https://bideo.rajit.cc/embed/spring-boot-seo-masterclass-a1b2", tw.get("twitter:player"));
    }

    @Test
    void testToIsoDuration() {
        assertEquals("PT0S", SeoMetadataBuilder.toIsoDuration(0));
        assertEquals("PT10S", SeoMetadataBuilder.toIsoDuration(10));
        assertEquals("PT2M30S", SeoMetadataBuilder.toIsoDuration(150));
        assertEquals("PT1H1M5S", SeoMetadataBuilder.toIsoDuration(3665));
    }
}
