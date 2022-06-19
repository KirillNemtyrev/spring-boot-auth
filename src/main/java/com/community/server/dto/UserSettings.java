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
    private String email;
    private String aboutMe;
    private String contactEmail;
    private String contactPhone;
    private String fileNameAvatar;

    private Date createDate;
}
