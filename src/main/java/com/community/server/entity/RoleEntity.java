package com.community.server.entity;

import com.community.server.enums.RoleNameEntity;
import org.hibernate.annotations.NaturalId;
import javax.persistence.*;

@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 60)
    private RoleNameEntity name;

    public RoleEntity() {}

    public RoleEntity(RoleNameEntity name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleNameEntity getName() {
        return name;
    }

    public void setName(RoleNameEntity name) {
        this.name = name;
    }
}
