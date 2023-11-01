package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ComposedProductionOrderDto {

    private Long id;
    private String code;
    private Date createdAt;
    private Date approvedAt;
    private Date hitInsertedAt;
}
