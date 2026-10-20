package com.video_streaming.project_video.Utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class SlugUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern MULTIDASH = Pattern.compile("-+");

    public static String generateSlug(String title) {
        String base = slugify(title);
        String uniqueSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return base + "-" + uniqueSuffix;
    }

    public static String slugify(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "video";
        }

        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = MULTIDASH.matcher(slug).replaceAll("-");
        slug = slug.replaceAll("^-|-$", "").toLowerCase(Locale.ENGLISH);

        if (slug.isEmpty()) {
            return "video";
        }
        return slug;
    }
}
