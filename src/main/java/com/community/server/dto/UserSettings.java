package com.community.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserSettings {

    private Long id;

    private String name;
    private String username;
    private String uuid;
    private String email;
    private String aboutMe;
    private String fileNameAvatar;
    private Date createDate;
}
