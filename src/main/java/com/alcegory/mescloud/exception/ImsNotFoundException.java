package com.alcegory.mescloud.exception;

import java.util.NoSuchElementException;

public class ImsNotFoundException extends NoSuchElementException {

    public ImsNotFoundException() {

    }

    public ImsNotFoundException(String message) {
        super(message);
    }
}
