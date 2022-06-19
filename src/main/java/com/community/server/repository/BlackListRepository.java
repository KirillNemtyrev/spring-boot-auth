package com.community.server.repository;

import com.community.server.entity.BlackListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlackListRepository  extends JpaRepository<BlackListEntity, Long> {

    Optional<BlackListEntity> findById(Long id);
    Optional<BlackListEntity> findByUserIdAndBanId(Long userId, Long banId);

    List<BlackListEntity> findByUserId(Long userId);
    List<BlackListEntity> findByBanId(Long banId);

    Boolean existsByUserIdAndBanId(Long userId, Long banId);
}
