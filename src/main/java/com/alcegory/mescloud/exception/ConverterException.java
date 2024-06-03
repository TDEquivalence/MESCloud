package com.alcegory.mescloud.exception;

public class ConverterException extends RuntimeException {
    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
