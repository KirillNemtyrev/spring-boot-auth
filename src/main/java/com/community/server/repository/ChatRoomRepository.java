package com.community.server.repository;

import com.community.server.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    Optional<ChatRoomEntity> findById(Long id);
    List<ChatRoomEntity> findBySenderIdOrRecipientId(Long senderId, Long recipientId);
    Boolean existsBySenderIdOrRecipientId(Long senderId, Long recipientId);
    Long countBySenderIdOrRecipientId(Long senderId, Long recipientId);
}
