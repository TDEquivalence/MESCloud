package com.tde.mescloud.service;

import com.amazonaws.services.iot.client.AWSIotMessage;

public interface MesProtocol {

    void react(AWSIotMessage message);
}
