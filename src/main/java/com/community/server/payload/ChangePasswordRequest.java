package com.community.server.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank
    @Size(max = 100)
    private String old_password;

    @NotBlank
    @Size(max = 100)
    private String new_password;

    public String getOld_password() {
        return old_password;
    }

    public String getNew_password() {
        return new_password;
    }
}
