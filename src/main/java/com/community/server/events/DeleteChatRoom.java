package com.community.server.events;

import com.community.server.enums.EventType;
import lombok.Getter;

@Getter
public class DeleteChatRoom {
    private final EventType eventType = EventType.DELETE_CHAT_ROOM;
    private Long id;

    public DeleteChatRoom() {}

    public DeleteChatRoom(Long id) {
        this.id = id;
    }
}
