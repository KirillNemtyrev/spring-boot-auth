package com.community.server.repository;

import com.community.server.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findByChatId(Long chatId);

    Optional<MessageEntity> findById(Long id);
    Optional<MessageEntity> findFirstByChatIdOrderByIdDesc(Long id);
}
