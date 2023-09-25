package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RequestEquipmentKpiDto {

    private Long equipmentId;
    private Date startDate;
    private Date endDate;
}
