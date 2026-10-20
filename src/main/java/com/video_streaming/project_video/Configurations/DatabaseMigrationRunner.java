package com.video_streaming.project_video.Configurations;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseMigrationRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrationRunner.class);
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            logger.info("Checking and applying database schema migrations for video SEO and metadata...");

            // 1. Add new columns if missing
            jdbcTemplate.execute("ALTER TABLE vid_metadata ADD COLUMN IF NOT EXISTS slug VARCHAR(255);");
            jdbcTemplate.execute("ALTER TABLE vid_metadata ADD COLUMN IF NOT EXISTS description TEXT;");
            jdbcTemplate.execute("ALTER TABLE vid_metadata ADD COLUMN IF NOT EXISTS category VARCHAR(50) DEFAULT 'OTHER';");
            jdbcTemplate.execute("ALTER TABLE vid_metadata ADD COLUMN IF NOT EXISTS visibility VARCHAR(50) DEFAULT 'PUBLIC';");
            jdbcTemplate.execute("ALTER TABLE vid_metadata ADD COLUMN IF NOT EXISTS iso_duration VARCHAR(50);");

            // 2. Backfill existing rows with defaults
            jdbcTemplate.execute("UPDATE vid_metadata SET visibility = 'PUBLIC' WHERE visibility IS NULL;");
            jdbcTemplate.execute("UPDATE vid_metadata SET category = 'OTHER' WHERE category IS NULL;");
            jdbcTemplate.execute("UPDATE vid_metadata SET slug = CONCAT('video-', video_id) WHERE slug IS NULL;");

            // 3. Create video_tags table if missing
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS video_tags (" +
                    "video_id BIGINT NOT NULL, " +
                    "tag VARCHAR(255) NOT NULL, " +
                    "CONSTRAINT fk_video_tags_video FOREIGN KEY (video_id) REFERENCES vid_metadata(video_id) ON DELETE CASCADE" +
                    ");");

            logger.info("Database schema migration completed successfully.");
        } catch (Exception e) {
            logger.warn("Schema migration notice (columns may already exist or running in restricted mode): {}", e.getMessage());
        }
    }
}
