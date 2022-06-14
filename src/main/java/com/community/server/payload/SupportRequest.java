package com.community.server.payload;

import javax.validation.constraints.NotBlank;

public class SupportRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getEmail() {
        return email;
    }
}
