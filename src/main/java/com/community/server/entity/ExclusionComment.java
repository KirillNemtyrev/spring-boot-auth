package com.community.server.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "exclusion_comment")
public class ExclusionComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long exclusionId;

    public ExclusionComment() {}

    public ExclusionComment(Long userId, Long exclusionId) {
        this.userId = userId;
        this.exclusionId = exclusionId;
    }
}
