package com.community.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoom {

    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String fileNameAvatar;
}
