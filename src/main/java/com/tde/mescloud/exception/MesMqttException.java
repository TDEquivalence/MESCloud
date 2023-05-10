package com.tde.mescloud.exception;

public class MesMqttException extends Exception {

    public MesMqttException(String errorMessage) {
        super(errorMessage);
    }

    public MesMqttException(Throwable exception) {
        super(exception);
    }
}
