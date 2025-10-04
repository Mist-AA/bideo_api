package com.video_streaming.project_video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableScheduling
public class ProjectVideoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectVideoApplication.class, args);
	}

}
