package com.community.server.events;

import com.community.server.enums.EventType;
import lombok.Getter;

import java.util.Date;

@Getter
public class MessageNew {
    private final EventType event = EventType.MESSAGE_NEW;
    private Long chatId;
    private String text;
    private Date sendDate;

    public MessageNew() {}
    public MessageNew(Long chatId, String text, Date sendDate){
        this.chatId = chatId;
        this.text = text;
        this.sendDate = sendDate;
    }
}
