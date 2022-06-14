package com.community.server.payload;

import lombok.Getter;
import org.springframework.lang.Nullable;
import javax.validation.constraints.*;

@Getter
public class SettingsRequest {

    @Size(max = 40)
    private String name;

    @Size(max = 40)
    private String username;

    @Size(max = 40)
    private String email;

    @Size(max = 11)
    private String phone;

    @Size(max = 20)
    private String type;

    @Size(max = 20)
    private String message;

    private Boolean showEmail;

    private Boolean showPhone;

    private Boolean showMessageRequest;

    private Boolean showMessageReaction;

    private Boolean notifyAction;

    private Boolean notifyRequest;

    private Boolean notifyMessage;

    private Boolean notifyEmail;

}
