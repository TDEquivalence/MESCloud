package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.equipment.EquipmentConfigMqttDto;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;

public interface PlcMqttConverter {

    EquipmentConfigMqttDto toMqttDto(CountingEquipmentEntity countingEquipment);
}
