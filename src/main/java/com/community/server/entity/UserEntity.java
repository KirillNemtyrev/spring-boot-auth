package com.community.server.entity;

import com.community.server.enums.CommentVisible;
import com.community.server.enums.ProfileStatisticVisible;
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
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NaturalId(mutable=true)
    private String uuid = UUID.randomUUID().toString();

    @NotBlank
    @Size(min=2, max = 40)
    private String name;

    @NotBlank
    @NaturalId(mutable=true)
    @Size(min=6, max = 40)
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

    @Size(max = 70)
    private String aboutMe;

    @NotBlank
    private String fileNameAvatar = "no_avatar.jpg";

    @NotBlank
    private String registerIP;

    private CommentVisible commentVisible = CommentVisible.ALL_VISION;

    @Nullable
    private Date recoveryDate;

    @Nullable
    private Date emailDate;

    @CreatedDate
    private Date createDate = new Date();

    @LastModifiedDate
    private Date lastModifyDate = new Date();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    private Boolean messagesInviteOnly = Boolean.FALSE;

    private ProfileStatisticVisible visibleMyChats = ProfileStatisticVisible.ALL_VISION;
    private ProfileStatisticVisible visibleMyInvite = ProfileStatisticVisible.ALL_VISION;

    public UserEntity() {}

    public UserEntity(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
