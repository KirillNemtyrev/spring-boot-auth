package com.community.server.repository;

import com.community.server.entity.SupportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportRepository extends JpaRepository<SupportEntity, Long> {

    Optional<SupportEntity> findByEmail(String email);
    Optional<SupportEntity> findById(Long id);

    List<SupportEntity> findByIdIn(List<Long> supportIds);
}
