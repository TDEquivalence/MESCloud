package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.model.dto.EquipmentConfigMqttDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.EquipmentOutputEntity;
import org.springframework.stereotype.Component;

import java.util.Comparator;
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

            equipmentConfig.setTotalOuput(equipmentOutputCodes.size());
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
