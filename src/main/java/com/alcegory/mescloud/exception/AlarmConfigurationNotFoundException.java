package com.alcegory.mescloud.exception;

import java.util.NoSuchElementException;

public class AlarmConfigurationNotFoundException extends NoSuchElementException {

    public AlarmConfigurationNotFoundException(String message) {
        super(message);
    }
}
