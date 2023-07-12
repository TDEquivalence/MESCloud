package com.tde.mescloud.constant;

public class MqttDTOConstants {

    private MqttDTOConstants() throws IllegalAccessException {
        String msg = String.format("[%s] is an utility class, thus should not be instantiated", this.getClass().getName());
        throw new IllegalAccessException(msg);
    }

    public static final String JSON_TYPE_PROPERTY_NAME = "jsonType";
    public static final String PRODUCTION_ORDER_DTO_NAME = "ProductionOrder";
    public static final String PRODUCTION_ORDER_RESPONSE_DTO_NAME = "ProductionOrderResponse";
    public static final String COUNTING_RECORD_DTO_NAME = "ProductionCount";
    public static final String HAS_RECEIVED_DTO_NAME = "Received";
    public static final String EQUIPMENT_CONFIG_DTO_NAME = "Configuration";
    public static final String EQUIPMENT_CONFIG_RESPONSE_DTO_NAME = "ConfigurationResponse";
}
