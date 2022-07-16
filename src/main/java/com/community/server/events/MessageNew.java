package com.community.server.events;

import com.community.server.enums.EventType;
import lombok.Getter;

import java.util.Date;

@Getter
public class MessageNew {
    private final int event = EventType.MESSAGE_NEW.ordinal();
    private Long chatId;
    private Long userId;
    private String text;
    private Date sendDate;
    private Long newCountMessages;

    public MessageNew() {}
    public MessageNew(Long chatId, Long userId, String text, Date sendDate, Long newCountMessages){
        this.chatId = chatId;
        this.userId = userId;
        this.text = text;
        this.sendDate = sendDate;
        this.newCountMessages = newCountMessages;
    }
}
