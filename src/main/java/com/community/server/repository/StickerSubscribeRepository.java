package com.community.server.repository;

import com.community.server.entity.StickerSubscribeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StickerSubscribeRepository extends JpaRepository<StickerSubscribeEntity, Long> {

    List<StickerSubscribeEntity> findByUserId(Long userId);

}
