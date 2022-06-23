package com.community.server.repository;

import com.community.server.entity.ExclusionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ExclusionCommentRepository extends JpaRepository<ExclusionComment, Long> {

    Optional<ExclusionComment> findByUserIdAndExclusionId(Long id, Long exclusionId);
    Boolean existsByUserIdAndExclusionId(Long userId, Long exclusionId);
}