package com.alcegory.mescloud.model.dto.alarm;

import com.alcegory.mescloud.constant.AlarmStatus;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.user.UserDto;
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
    private Date recognizedAt;
    private UserDto recognizedBy;
}
