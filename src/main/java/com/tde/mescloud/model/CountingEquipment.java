package com.tde.mescloud.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CountingEquipment {

    private long id;
    private String code;
    private String alias;
    private int equipmentStatus;
    private int pTimerCommunicationCycle;
    List<EquipmentOutput> outputs;
    private boolean hasActiveProductionOrder;
}
