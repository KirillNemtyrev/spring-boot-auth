package com.community.server.events;

import com.community.server.enums.EventType;
import lombok.Getter;

@Getter
public class DeleteChatRoom {
    private final int event = EventType.DELETE_CHAT_ROOM.ordinal();
    private Long chatId;

    public DeleteChatRoom() {}

    public DeleteChatRoom(Long chatId) {
        this.chatId = chatId;
    }
}
