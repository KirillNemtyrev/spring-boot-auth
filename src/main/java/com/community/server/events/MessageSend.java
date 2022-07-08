package com.community.server.events;

import com.community.server.enums.EventType;
import lombok.Getter;

import java.util.Date;

@Getter
public class MessageSend {
    private final EventType event = EventType.MESSAGE_SEND;
    private Long chatId;
    private String text;
    private Date sendDate;

    public MessageSend() {}
    public MessageSend(Long chatId, String text, Date sendDate){
        this.chatId = chatId;
        this.text = text;
        this.sendDate = sendDate;
    }
}
