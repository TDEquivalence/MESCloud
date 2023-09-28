package com.alcegory.mescloud.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EquipmentStatus {

    STOPPED(0),
    ACTIVE(1),
    ALARM(2);

    final int status;

    public static EquipmentStatus getByStatus(int status) {
        for (EquipmentStatus equipmentStatus : EquipmentStatus.values()) {
            if (equipmentStatus.status == status) {
                return equipmentStatus;
            }
        }

        throw new IllegalArgumentException("No EquipmentStatus found with status: " + status);
    }
}