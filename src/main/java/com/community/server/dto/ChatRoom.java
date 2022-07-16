package com.community.server.dto;

import com.community.server.entity.MessageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ChatRoom {

    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String fileNameAvatar;

    private Long lastSenderId;
    private String lastMessage;
    private Long lastMessageDate;
    private Long countNewMessage;
}
