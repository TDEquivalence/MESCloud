package com.alcegory.mescloud.model.dto;

import com.alcegory.mescloud.constant.AlarmStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AlarmDto {

    private Long id;
    private AlarmConfigurationDto alarmConfiguration;
    private CountingEquipmentDto equipment;
    private ProductionOrderDto productionOrder;
    private AlarmStatus status;
    private String comment;
    private Date createdAt;
    private Date completedAt;
    private UserDto completedBy;
}
