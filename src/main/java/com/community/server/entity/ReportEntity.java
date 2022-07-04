package com.community.server.entity;

import com.community.server.enums.ReportType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "reports_user")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long suspectId;

    @NotNull
    private ReportType reportType;

    @NotNull
    @Size(min=100, max=1000)
    private String comment;

    @CreatedDate
    private Date createDate = new Date();

    public ReportEntity() {}

    public ReportEntity(Long userId, Long suspectId, ReportType reportType, String comment){
        this.userId = userId;
        this.suspectId = suspectId;
        this.reportType = reportType;
        this.comment = comment;
    }
}
