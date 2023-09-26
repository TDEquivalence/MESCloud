package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

@Getter
@Setter
public class RequestKpiDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Timestamp startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Timestamp endDate;
}
