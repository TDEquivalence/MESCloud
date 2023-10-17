package com.alcegory.mescloud.utility;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpUtil {

    private HttpUtil() {
        //Utility class, not meant for instantiation
    }

    public static final String ERROR_CAUSE_HEADER = "Error-Cause";
    public static final String ERROR_MESSAGE_HEADER = "Error-Message";

    public static <T> ResponseEntity<T> responseWithHeaders(HttpStatus httpStatus, String errorCause, String errorMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ERROR_CAUSE_HEADER, errorCause);
        headers.add(ERROR_MESSAGE_HEADER, errorMessage);
        return ResponseEntity.status(httpStatus)
                .headers(headers)
                .build();
    }

    public static <T, E extends Exception> ResponseEntity<T> responseWithHeaders(HttpStatus httpStatus, String errorCause, E e) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ERROR_CAUSE_HEADER, errorCause);
        headers.add(ERROR_MESSAGE_HEADER, e.getMessage());
        return ResponseEntity.status(httpStatus)
                .headers(headers)
                .build();
    }
}

