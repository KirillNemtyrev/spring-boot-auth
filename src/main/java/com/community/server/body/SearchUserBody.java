package com.community.server.body;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class SearchUserBody {
    @NotNull
    private String nameOrUsername;
}
