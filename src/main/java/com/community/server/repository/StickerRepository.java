package com.community.server.repository;

import com.community.server.entity.StickerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StickerRepository extends JpaRepository<StickerEntity, Long> {

    List<StickerEntity> findByStickerPack(Long stickerPack);

}
