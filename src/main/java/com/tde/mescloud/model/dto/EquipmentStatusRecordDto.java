package com.tde.mescloud.model.dto;

import com.tde.mescloud.constant.EquipmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EquipmentStatusRecordDto {

    private Long id;
    private Long countingEquipmentId;
    private EquipmentStatus equipmentStatus;
    private Date registeredAt;
}
