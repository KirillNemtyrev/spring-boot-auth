package com.community.server.payload;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class SupportRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String title;

    @NotBlank
    private String message;
}
