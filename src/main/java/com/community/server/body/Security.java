package com.community.server.body;

import lombok.Getter;

@Getter
public class Security {
    private String oldPassword;
    private String newPassword;
    private String oldEmail;
    private String newEmail;
    private String code;
}
