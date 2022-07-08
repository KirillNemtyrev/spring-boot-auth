package com.community.server.body;

import lombok.Getter;

import javax.validation.constraints.Size;

@Getter
public class Settings {

    @Size(min=2, max = 40)
    private String name;

    @Size(min=6, max = 40)
    private String username;

    @Size(max=70)
    private String aboutMe;
}
