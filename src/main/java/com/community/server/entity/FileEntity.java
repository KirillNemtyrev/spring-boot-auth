package com.community.server.entity;

import com.community.server.enums.TypeFile;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.FileWriter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String file;

    private Long author;

    @CreatedDate
    private Date created;

    private TypeFile typeFile;

    public FileEntity(){}

    public FileEntity(String file, Long author, Date created, TypeFile typeFile) {
        this.file = file;
        this.author = author;
        this.created = created;
        this.typeFile = typeFile;
    }
}
