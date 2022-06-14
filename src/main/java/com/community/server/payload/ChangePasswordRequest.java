package com.community.server.payload;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class ChangePasswordRequest {

    @NotBlank
    @Size(max = 100)
    private String passwordOld;

    @NotBlank
    @Size(max = 100)
    private String passwordNew;
}
