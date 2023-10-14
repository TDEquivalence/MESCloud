package com.alcegory.mescloud.exception;

import java.util.NoSuchElementException;

public class AlarmRecordNotFoundException extends NoSuchElementException {

    public AlarmRecordNotFoundException(String message) {
        super(message);
    }
}
