package com.community.server.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class ChangePasswordDto {

    @NotBlank
    @Size(max = 100)
    private String passwordOld;

    @NotBlank
    @Size(max = 100)
    private String passwordNew;
}
