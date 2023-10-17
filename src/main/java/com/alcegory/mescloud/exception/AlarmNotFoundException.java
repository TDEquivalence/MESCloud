package com.alcegory.mescloud.exception;

import java.util.NoSuchElementException;

public class AlarmNotFoundException extends NoSuchElementException {

    public AlarmNotFoundException(String message) {
        super(message);
    }
}
