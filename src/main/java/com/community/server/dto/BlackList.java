package com.community.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlackList {
    private Long id;
    private String username;
    private String name;
    private String fileNameAvatar;
}
