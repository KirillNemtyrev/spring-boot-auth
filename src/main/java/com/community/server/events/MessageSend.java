package com.community.server.events;

import com.community.server.enums.EventType;
import lombok.Getter;

import java.util.Date;

@Getter
public class MessageSend {
    private final int event = EventType.MESSAGE_SEND.ordinal();
    private Long chatId;
    private Long userId;
    private String text;
    private Date sendDate;

    public MessageSend() {}
    public MessageSend(Long chatId, Long userId, String text, Date sendDate){
        this.userId = userId;
        this.chatId = chatId;
        this.text = text;
        this.sendDate = sendDate;
    }
}
