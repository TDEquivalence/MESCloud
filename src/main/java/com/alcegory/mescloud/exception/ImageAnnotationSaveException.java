package com.alcegory.mescloud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ImageAnnotationSaveException extends RuntimeException {

    public ImageAnnotationSaveException(String message) {
        super(message);
    }

    public ImageAnnotationSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
