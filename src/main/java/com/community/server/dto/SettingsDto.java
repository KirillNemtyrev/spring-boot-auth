package com.community.server.dto;

import com.community.server.enums.CensoredMessageEntity;
import com.community.server.enums.TypePrivateEntity;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Size;

@Getter
public class SettingsDto {

    @Size(max = 40)
    private String name;

    @Size(max = 40)
    private String username;

    @Size(max = 40)
    private String contactEmail;

    @Size(max = 11)
    private String contactPhone;

    private Boolean showContactEmail;
    private Boolean showContactPhone;

    private Boolean showMessageRequest;
    private Boolean showMessageReaction;
    private Boolean notifyAction;
    private Boolean notifyRequest;
    private Boolean notifyMessage;
    private Boolean notifyEmail;

    private TypePrivateEntity type;
    private CensoredMessageEntity message;

}
