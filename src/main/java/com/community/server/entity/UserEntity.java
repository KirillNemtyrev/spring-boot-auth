package com.community.server.entity;

import com.community.server.enums.CensoredMessageEntity;
import com.community.server.enums.TypePrivateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
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
    private String recoveryCode;

    @Nullable
    private Date recoveryDate;

    @CreatedDate
    private Date createDate = new Date();

    @Size(max = 40)
    private String contactEmail;

    @Size(max = 40)
    private String contactPhone;

    @NotBlank
    private String fileNameAvatar = "no_avatar.jpg";

    private Boolean showContactEmail = Boolean.FALSE;
    private Boolean showContactPhone = Boolean.FALSE;
    private Boolean showMessageRequest = Boolean.TRUE;
    private Boolean showMessageReaction = Boolean.TRUE;
    private Boolean notifyAction = Boolean.TRUE;
    private Boolean notifyRequest = Boolean.TRUE;
    private Boolean notifyMessage = Boolean.TRUE;
    private Boolean notifyEmail = Boolean.TRUE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    private TypePrivateEntity type = TypePrivateEntity.TYPE_PUBLIC;
    private CensoredMessageEntity message = CensoredMessageEntity.CENSORED_MESSAGE;

    public UserEntity() {}

    public UserEntity(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
