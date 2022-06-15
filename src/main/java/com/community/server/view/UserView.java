package com.community.server.view;

import com.community.server.enums.CensoredMessageEntity;
import com.community.server.enums.TypePrivateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserView {

    private Long id;

    private String name;
    private String username;
    private String email;
    private String contactEmail;
    private String contactPhone;
    private String fileNameAvatar;

    private Date createDate;

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
