package com.community.server.repository;

import com.community.server.entity.StickersListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StickerListRepository extends JpaRepository<StickersListEntity, Long> {

    Optional<StickersListEntity> findById(Long id);
}
