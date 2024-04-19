package com.alcegory.mescloud.exception;

public class DeleteUserException extends RuntimeException {

    public DeleteUserException(String message) {
        super(message);
    }

    public DeleteUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
