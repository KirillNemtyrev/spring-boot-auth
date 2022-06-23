package com.community.server.repository;

import com.community.server.entity.InviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<InviteEntity, Long> {
    Long countByUserId(Long userId);
}
