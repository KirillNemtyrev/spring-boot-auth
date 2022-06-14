package com.community.server.payload;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class RecoverySendRequest {

    @NotBlank
    private String email;
}
