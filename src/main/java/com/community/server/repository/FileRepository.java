package com.community.server.repository;

import com.community.server.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findById(Long id);
    List<FileEntity> findByAuthor(Long authorId);
}
