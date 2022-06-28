package com.community.server.dto;

import com.community.server.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class UserSearch {
    private Long id;
    private Long countChats = 0L;
    private Long countInvite = 0L;

    private String name;
    private String username;
    private String aboutMe;
    private String fileNameAvatar;
    private String contactEmail;
    private String contactPhone;

    private Boolean messagesInviteOnly;

    @Nullable
    private UserStatus userStatus;

    public UserSearch() {}
    public UserSearch(Long id, String name, String username, String aboutMe, String fileNameAvatar, String contactEmail, String contactPhone ){
        this.id = id;
        this.name = name;
        this.username = username;
        this.aboutMe = aboutMe;
        this.fileNameAvatar = fileNameAvatar;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }
}
