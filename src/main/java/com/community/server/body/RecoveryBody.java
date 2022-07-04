package com.community.server.body;

import lombok.Getter;

@Getter
public class RecoveryBody {
    private String email;
    private String password;
    private String code;
}
