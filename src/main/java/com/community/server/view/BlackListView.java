package com.community.server.view;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlackListView {

    private Long id;
    private String name;
    private String username;
    private String fileNameAvatar;

}
