package com.community.server.entity;

import com.community.server.enums.CensoredMessageEntity;
import com.community.server.enums.TypePrivateEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Data
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

    @NaturalId(mutable=true)
    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank
    @Size(max = 100)
    private String password;

    @Nullable
    @Size(max = 6)
    private String recoveryCode;

    @Nullable
    @Size(max = 6)
    private String emailCode;

    @Size(max = 40)
    private String contactEmail;

    @Size(max = 40)
    private String contactPhone;

    @NotBlank
    private String fileNameAvatar = "no_avatar.jpg";

    @Nullable
    private Date recoveryDate;

    @Nullable
    private Date emailDate;

    @CreatedDate
    private Date createDate = new Date();

    @LastModifiedDate
    private Date lastModifyDate;

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
