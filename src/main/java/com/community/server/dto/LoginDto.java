package com.community.server.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginDto {

    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
