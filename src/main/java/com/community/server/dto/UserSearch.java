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
    private Boolean messagesInviteOnly;
    private Boolean inBlackList = Boolean.FALSE;

    @Nullable
    private UserStatus userStatus;

    public UserSearch() {}
    public UserSearch(Long id, String name, String username, String aboutMe, String fileNameAvatar){
        this.id = id;
        this.name = name;
        this.username = username;
        this.aboutMe = aboutMe;
        this.fileNameAvatar = fileNameAvatar;
    }
}
