package com.community.server.payload;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class RecoveryChangeRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String code;

    @NotBlank
    private String password;
}
