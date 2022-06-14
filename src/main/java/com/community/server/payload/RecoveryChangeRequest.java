package com.community.server.payload;

import javax.validation.constraints.NotBlank;

public class RecoveryChangeRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String code;

    @NotBlank
    private String new_password;

    public String getNew_password() {
        return new_password;
    }

    public String getCode() {
        return code;
    }

    public String getEmail() {
        return email;
    }
}
