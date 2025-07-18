package com.video_streaming.project_video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class}
)
public class ProjectVideoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectVideoApplication.class, args);
	}

}
