package com.community.server.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class SupportDto {
    @NotBlank
    private String email;

    @NotBlank
    private String title;

    @NotBlank
    private String message;
}
