package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.model.entity.SectionEntity;

import java.util.List;

public interface CountingEquipmentProjection {
    long getId();
    String getCode();
    String getAlias();
    SectionEntity getSection();
    int getEquipmentStatus();
    int getPTimerCommunicationCycle();
    boolean isHasActiveProductionOrder();
    List<EquipmentOutputEntity> getOutputs();
}
