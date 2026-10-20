package com.video_streaming.project_video.Utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SlugUtilsTest {

    @Test
    void testSlugifyCleanText() {
        String slug = SlugUtils.slugify("Building A Modern Video Streaming Platform");
        assertEquals("building-a-modern-video-streaming-platform", slug);
    }

    @Test
    void testSlugifySpecialCharactersAndAccents() {
        String slug = SlugUtils.slugify("Héllo Wörld! @ bideo.tech #2026 ??");
        assertEquals("hello-world-bideotech-2026", slug);
    }

    @Test
    void testSlugifyEmptyAndNull() {
        assertEquals("video", SlugUtils.slugify(""));
        assertEquals("video", SlugUtils.slugify("   "));
        assertEquals("video", SlugUtils.slugify(null));
    }

    @Test
    void testGenerateSlugIncludesSuffix() {
        String slug1 = SlugUtils.generateSlug("Cool Demo");
        String slug2 = SlugUtils.generateSlug("Cool Demo");

        assertTrue(slug1.startsWith("cool-demo-"));
        assertTrue(slug2.startsWith("cool-demo-"));
        assertNotEquals(slug1, slug2, "Slugs generated from the same title should have distinct unique suffixes");
    }
}
