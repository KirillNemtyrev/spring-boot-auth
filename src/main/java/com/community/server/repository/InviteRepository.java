package com.community.server.repository;

import com.community.server.entity.InviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<InviteEntity, Long> {
    Long countByUserId(Long userId);
    Boolean existsByUserIdAndInviteId(Long userId, Long inviteId);
}
