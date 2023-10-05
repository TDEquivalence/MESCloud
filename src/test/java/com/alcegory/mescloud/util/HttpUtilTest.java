package com.alcegory.mescloud.util;

import com.alcegory.mescloud.utility.HttpUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HttpUtilTest {

    private static final String ERROR_CAUSE = "Resource not found";
    private static final String ERROR_MESSAGE = "The resource does not exist.";

    @Test
    void testNotFound() {
        ResponseEntity<String> response = HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, ERROR_CAUSE, ERROR_MESSAGE);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        HttpHeaders headers = response.getHeaders();
        assertEquals(ERROR_CAUSE, headers.getFirst(HttpUtil.ERROR_CAUSE_HEADER));
        assertEquals(ERROR_MESSAGE, headers.getFirst(HttpUtil.ERROR_MESSAGE_HEADER));

        assertNull(response.getBody());
    }

    @Test
    void testNotFoundWithGenericException() {
        NoSuchElementException customException = new NoSuchElementException(ERROR_MESSAGE);
        ResponseEntity<String> response = HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, ERROR_CAUSE, customException);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        HttpHeaders headers = response.getHeaders();
        assertEquals(ERROR_CAUSE, headers.getFirst(HttpUtil.ERROR_CAUSE_HEADER));
        assertEquals(ERROR_MESSAGE, headers.getFirst(HttpUtil.ERROR_MESSAGE_HEADER));

        assertNull(response.getBody());
    }
}