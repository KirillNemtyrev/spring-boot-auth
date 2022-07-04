package com.community.server.body;

import com.community.server.enums.ReportType;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class ReportBody {

    @NotNull
    private Long suspectId;

    @NotNull
    private ReportType reportType;

    @NotNull
    @Size(min=100, max=1000)
    private String comment;

}
