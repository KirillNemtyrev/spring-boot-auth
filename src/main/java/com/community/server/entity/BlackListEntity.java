package com.community.server.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "blacklist")
public class BlackListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long banId;

    @CreatedDate
    private Date date = new Date();

    public BlackListEntity() {}

    public BlackListEntity(Long userId, Long banId) {
        this.userId = userId;
        this.banId = banId;
    }
}
