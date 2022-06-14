package com.community.server.payload;

import javax.validation.constraints.NotBlank;

public class RecoverySendRequest {

    @NotBlank
    private String email;

    public String getEmail() {
        return email;
    }
}
