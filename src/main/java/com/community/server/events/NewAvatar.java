package com.community.server.events;

import com.community.server.enums.EventType;
import lombok.Getter;

@Getter
public class NewAvatar {
    private final EventType eventType = EventType.NEW_AVATAR;
    private String fileNameAvatar;

    public NewAvatar() {}

    public NewAvatar(String fileNameAvatar) {
        this.fileNameAvatar = fileNameAvatar;
    }
}
