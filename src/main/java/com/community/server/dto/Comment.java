package com.community.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Comment {

    private Long id;
    private Long authorId;
    private String authorName;
    private String authorUsername;
    private String authorFileNameAvatar;
    private String comment;
    private Boolean accessForRemove;
    private Date date;
}
