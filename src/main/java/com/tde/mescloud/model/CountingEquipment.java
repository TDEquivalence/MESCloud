package com.tde.mescloud.model;

import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CountingEquipment {

    private long id;
    private String code;
    private String alias;
    //private Section section;
    private int equipmentStatus;
    private int pTimerCommunicationCycle;
    List<EquipmentOutput> outputs;
    private boolean hasActiveProductionOrder;

    public CountingEquipment(CountingEquipmentEntity entity) {

        if (entity == null) {
            return;
        }

        this.id = entity.getId();
        this.equipmentStatus = entity.getEquipmentStatus();
        this.code = entity.getCode();
        this.alias = entity.getAlias();
        this.pTimerCommunicationCycle = entity.getPTimerCommunicationCycle();
        this.outputs = new ArrayList<>(entity.getOutputs().size());
    }
}
