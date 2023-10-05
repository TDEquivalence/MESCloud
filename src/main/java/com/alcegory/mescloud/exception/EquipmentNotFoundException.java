package com.alcegory.mescloud.exception;

import java.util.NoSuchElementException;

public class EquipmentNotFoundException extends NoSuchElementException {

    public EquipmentNotFoundException() {

    }

    public EquipmentNotFoundException(String message) {
        super(message);
    }
}
