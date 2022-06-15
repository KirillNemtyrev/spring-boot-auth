package com.community.server.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class RecoveryChangeDto {

    @NotBlank
    private String email;

    @NotBlank
    private String code;

    @NotBlank
    private String password;
}
