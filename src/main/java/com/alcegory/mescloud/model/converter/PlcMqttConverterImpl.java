package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.model.dto.equipment.EquipmentConfigMqttDto;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.equipment.EquipmentOutputEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlcMqttConverterImpl implements PlcMqttConverter {

    @Override
    public EquipmentConfigMqttDto toMqttDto(CountingEquipmentEntity countingEquipment) {
        EquipmentConfigMqttDto equipmentConfig = new EquipmentConfigMqttDto();
        equipmentConfig.setJsonType(MqttDTOConstants.EQUIPMENT_CONFIG_DTO_NAME);
        equipmentConfig.setEquipmentCode(countingEquipment.getCode());
        equipmentConfig.setPTimerCommunicationCycle(countingEquipment.getPTimerCommunicationCycle());

        if (countingEquipment.getOutputs() != null) {
            List<String> equipmentOutputCodes = getSortedEquipmentOutputCodes(countingEquipment);

            equipmentConfig.setTotalOutput(equipmentOutputCodes.size());
            equipmentConfig.setOutputCodes(equipmentOutputCodes.toArray(new String[0]));
        }

        return equipmentConfig;
    }

    private List<String> getSortedEquipmentOutputCodes(CountingEquipmentEntity countingEquipment) {
        return countingEquipment.getOutputs()
                .stream()
                .map(EquipmentOutputEntity::getCode)
                .sorted()
                .toList();
    }

}
