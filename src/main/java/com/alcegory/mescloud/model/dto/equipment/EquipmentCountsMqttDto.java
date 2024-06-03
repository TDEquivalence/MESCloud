package com.alcegory.mescloud.model.dto.equipment;

import com.alcegory.mescloud.model.dto.mqqt.AbstractMqttDto;
import com.alcegory.mescloud.model.dto.mqqt.CounterMqttDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentCountsMqttDto extends AbstractMqttDto {

    private String equipmentCode;
    private String productionOrderCode;
    private int equipmentStatus;
    private CounterMqttDto[] counters;
}
