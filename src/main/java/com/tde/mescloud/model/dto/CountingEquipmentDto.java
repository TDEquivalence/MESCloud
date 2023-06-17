package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CountingEquipmentDto {

    private long id;
    private String code;
    private String alias;
    //private Section section;
    private int equipmentStatus;
    private int pTimerCommunicationCycle;
    private boolean hasActiveProductionOrder;
    private List<EquipmentOutputDto> outputs;
}
