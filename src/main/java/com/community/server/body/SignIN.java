package com.community.server.body;

import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class SignIN {

    @Email
    private String UsernameOremail;
    private String password;
}
