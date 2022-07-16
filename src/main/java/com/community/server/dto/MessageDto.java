package com.community.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String text;
    private Long date;
}
