package com.alcegory.mescloud.exception;

public class ExcelExportException extends RuntimeException {
    public ExcelExportException(String message) {
        super(message);
    }

    public ExcelExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
