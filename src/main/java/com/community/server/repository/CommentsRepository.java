package com.community.server.repository;

import com.community.server.dto.Comment;
import com.community.server.entity.CommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<CommentsEntity, Long> {
    List<CommentsEntity> findByUserId(Long userId);
    Long countByUserId(Long userId);
}
