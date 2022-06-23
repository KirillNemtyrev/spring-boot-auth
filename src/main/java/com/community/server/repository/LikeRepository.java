package com.community.server.repository;

import com.community.server.entity.LikesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<LikesEntity, Long> {

    Long countByUserId(Long userId);
}
