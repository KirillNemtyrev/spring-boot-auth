package com.community.server.payload;

import org.springframework.lang.Nullable;
import javax.validation.constraints.*;

public class SettingsRequest {

    @Size(max = 40)
    private String name;

    @Size(max = 40)
    private String username;

    @Nullable
    private Boolean show_email;

    @Nullable
    private Boolean show_phone;

    @Nullable
    private Boolean show_message_request;

    @Nullable
    private Boolean show_message_reaction;

    @Size(max = 40)
    private String email;

    @Size(max = 11)
    private String phone;

    @Nullable
    private Boolean notification_action;

    @Nullable
    private Boolean notification_request;

    @Nullable
    private Boolean notification_message;

    @Nullable
    private Boolean notification_email;

    @Size(max = 20)
    private String privacy;

    @Size(max = 20)
    private String censored;

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Boolean isShow_phone() {
        return show_phone;
    }

    public Boolean isShow_email() {
        return show_email;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public Boolean isNotification_action() {
        return notification_action;
    }

    public Boolean isNotification_request() {
        return notification_request;
    }

    public Boolean isNotification_message() {
        return notification_message;
    }

    public Boolean isNotification_email() {
        return notification_email;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getCensored() {
        return censored;
    }

    public Boolean isShowMessageRequest() {
        return show_message_request;
    }

    public Boolean isShowMessageReaction() {
        return show_message_reaction;
    }
}
