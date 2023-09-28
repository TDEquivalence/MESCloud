package com.alcegory.mescloud.protocol;

import com.amazonaws.services.iot.client.AWSIotMessage;

public interface MesProtocol {

    void react(AWSIotMessage message);
}
