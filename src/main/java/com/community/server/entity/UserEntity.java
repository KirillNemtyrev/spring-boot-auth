package com.community.server.entity;

import com.community.server.enums.CensoredMessageEntity;
import com.community.server.enums.TypePrivateEntity;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 40)
    private String name;

    @NotBlank
    @Size(max = 40)
    private String username;

    @NaturalId
    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank
    @Size(max = 100)
    private String password;

    @NotBlank
    @Size(max = 6)
    private String resetcode;

    @Null
    private Date reset_time;

    @CreatedDate
    private Date create_time;

    private TypePrivateEntity private_status;

    @Size(max = 40)
    private String contact_email;

    @Size(max = 40)
    private String contact_phone;

    @NotBlank
    private String path_avatar;

    private boolean show_email;

    private boolean show_phone;

    private boolean show_message_request;

    private boolean show_message_reaction;

    private CensoredMessageEntity censoredMessage;

    private boolean notification_action;

    private boolean notification_request;

    private boolean notification_message;

    private boolean notification_email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    public UserEntity() {}

    public UserEntity(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public String getResetcode() {
        return resetcode;
    }

    public void setResetcode(String resetcode) {
        this.resetcode = resetcode;
    }

    public Date getReset_time() {
        return reset_time;
    }

    public void setReset_time(Date reset_time) {
        this.reset_time = reset_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public TypePrivateEntity getPrivate_status() {
        return private_status;
    }

    public void setPrivate_status(TypePrivateEntity private_status) {
        this.private_status = private_status;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public String getContact_phone() {
        return contact_phone;
    }

    public void setContact_phone(String contact_phone) {
        this.contact_phone = contact_phone;
    }

    public CensoredMessageEntity getCensoredMessage() {
        return censoredMessage;
    }

    public void setCensoredMessage(CensoredMessageEntity censoredMessage) {
        this.censoredMessage = censoredMessage;
    }

    public String getPath_avatar() {
        return path_avatar;
    }

    public void setPath_avatar(String path_avatar) {
        this.path_avatar = path_avatar;
    }

    public boolean isShow_phone() {
        return show_phone;
    }

    public void setShow_phone(boolean show_phone) {
        this.show_phone = show_phone;
    }

    public boolean isShow_email() {
        return show_email;
    }

    public void setShow_email(boolean show_email) {
        this.show_email = show_email;
    }

    public boolean isNotification_email() {
        return notification_email;
    }

    public void setNotification_email(boolean notification_email) {
        this.notification_email = notification_email;
    }

    public boolean isNotification_message() {
        return notification_message;
    }

    public void setNotification_message(boolean notification_message) {
        this.notification_message = notification_message;
    }

    public boolean isNotification_request() {
        return notification_request;
    }

    public void setNotification_request(boolean notification_request) {
        this.notification_request = notification_request;
    }

    public boolean isNotification_action() {
        return notification_action;
    }

    public void setNotification_action(boolean notification_action) {
        this.notification_action = notification_action;
    }

    public boolean isShow_message_request() {
        return show_message_request;
    }

    public void setShow_message_request(boolean show_message_request) {
        this.show_message_request = show_message_request;
    }

    public boolean isShow_message_reaction() {
        return show_message_reaction;
    }

    public void setShow_message_reaction(boolean show_message_reaction) {
        this.show_message_reaction = show_message_reaction;
    }
}
