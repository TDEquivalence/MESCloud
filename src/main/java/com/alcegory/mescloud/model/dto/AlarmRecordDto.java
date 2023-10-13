package com.alcegory.mescloud.model.dto;

import com.alcegory.mescloud.constant.AlarmStatus;

import java.util.Date;

public class AlarmRecordDto {

    private Long id;
    private AlarmDto alarm;
    private ProductionOrderDto productionOrder;
    private AlarmStatus status;
    private String comment;
    private Date createdAt;
    private Date completedAt;
    private UserDto completedBy;
}
