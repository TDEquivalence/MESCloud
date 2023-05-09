package com.tde.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tde.mescloud.constant.MqttDTOConstants;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = MqttDTOConstants.JSON_TYPE_PROPERTY_NAME,
        visible = true)
@JsonSubTypes({
        @Type(value = HasReceivedMqttDTO.class, name = MqttDTOConstants.HAS_RECEIVED_DTO_NAME),
        @Type(value = EquipmentConfigMqttDTO.class, name = MqttDTOConstants.EQUIPMENT_CONFIG_DTO_NAME),
        @Type(value = EquipmentCountsMqttDTO.class,
                names = {MqttDTOConstants.COUNTING_RECORD_DTO_NAME, MqttDTOConstants.PRODUCTION_ORDER_INIT_DTO_NAME}),
        @Type(value = ProductionOrderMqttDTO.class, name = MqttDTOConstants.PRODUCTION_ORDER_DTO_NAME)
})
public interface MqttDTO {

    String getJsonType();
    //TODO: Check if this property should be declared at interface level, abstract class level, or not declared at all
    String getEquipmentCode();
}
