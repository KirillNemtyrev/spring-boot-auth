package com.community.server.entity;

import com.community.server.enums.ChatRoomVisible;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "chat_rooms")
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long senderId;

    @NotNull
    private Long recipientId;

    private ChatRoomVisible chatRoomVisible = ChatRoomVisible.ALL_VISION;

    @CreatedDate
    private Date createdDate = new Date();

    private Long countNewMessage;

    public ChatRoomEntity() {}

    public ChatRoomEntity(Long senderId, Long recipientId) {
        this.senderId = senderId;
        this.recipientId = recipientId;
    }
}
