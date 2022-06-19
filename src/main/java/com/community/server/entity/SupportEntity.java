package com.community.server.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@Table(name = "support")
public class SupportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String title;

    @NotBlank
    @Size(min = 100, max = 1000)
    private String message;

    public SupportEntity(){

    }

    public SupportEntity(String email, String title, String message){
        this.email = email;
        this.title = title;
        this.message = message;
    }
}
