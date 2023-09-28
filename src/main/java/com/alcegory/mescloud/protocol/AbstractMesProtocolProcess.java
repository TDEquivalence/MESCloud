package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.model.dto.MqttDto;
import jakarta.annotation.PostConstruct;

public abstract class AbstractMesProtocolProcess<T extends MqttDto> implements MesProtocolProcess<T>{

    @PostConstruct
    private void registerMesProtocolProcess() {
        AbstractMesProtocol.registerMesProcess(this.getMatchingDTOName(), this);
    }

    public abstract String getMatchingDTOName();
}
