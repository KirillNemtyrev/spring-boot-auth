package com.community.server.dto;

import com.community.server.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@Data
public class UserSearch {
    private Long id;
    private Long countChats;
    private Long countInvite;
    private Long countLike;

    private String name;
    private String username;
    private String aboutMe;
    private String fileNameAvatar;
    private String contactEmail;
    private String contactPhone;

    @Nullable
    private UserStatus userStatus;
}
