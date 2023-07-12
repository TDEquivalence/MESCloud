package com.tde.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "counter_record_production_conclusion")
@Getter
@Setter
public class CounterRecordConclusionEntity {

    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_output_id")
    private EquipmentOutputEntity equipmentOutput;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id")
    private ProductionOrderEntity productionOrder;
    private int realValue;
    private int computedValue;
    private Date registeredAt;
}
