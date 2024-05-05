package com.alcegory.mescloud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ImageAnnotationException extends RuntimeException {

    public ImageAnnotationException(String message) {
        super(message);
    }

    public ImageAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }
}
