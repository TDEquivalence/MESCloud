package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.EquipmentConfigMqttDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;

public interface PlcMqttConverter {

    EquipmentConfigMqttDto toMqttDto(CountingEquipmentEntity countingEquipment);
}
