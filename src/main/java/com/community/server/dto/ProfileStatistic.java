package com.community.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileStatistic {
    private Long userId;
    private String username;
    private String name;
    private String fileNameAvatar;
}