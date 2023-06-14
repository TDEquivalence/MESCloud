package com.tde.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.util.Date;

@Entity(name = "counter_record")
@Getter
@Setter
public class CounterRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private EquipmentOutputEntity equipmentOutput;
    private String equipmentOutputAlias;
    private int realValue;
    private int computedValue;
    @ManyToOne(fetch = FetchType.EAGER)
    private ProductionOrderEntity productionOrder;
    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;
}
