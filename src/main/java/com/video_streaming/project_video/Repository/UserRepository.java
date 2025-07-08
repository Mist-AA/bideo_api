package com.video_streaming.project_video.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.video_streaming.project_video.Entity.User;

public interface UserRepository extends JpaRepository<User, String> {}