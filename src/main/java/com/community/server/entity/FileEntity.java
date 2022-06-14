package com.community.server.entity;

import com.community.server.enums.TypeFile;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.FileWriter;
import java.util.Date;

@Entity
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

    private TypeFile type_file;

    public FileEntity(String file, Long author, Date created, TypeFile type_file) {
        this.file = file;
        this.author = author;
        this.created = created;
        this.type_file = type_file;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public TypeFile getType_file() {
        return type_file;
    }

    public void setType_file(TypeFile type_file) {
        this.type_file = type_file;
    }

    public Long getId() {
        return id;
    }

    public Long getAuthor() {
        return author;
    }

    public void setAuthor(Long author) {
        this.author = author;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
