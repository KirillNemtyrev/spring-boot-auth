package com.community.server.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class RecoveryDto {

    @NotBlank
    private String email;
}
