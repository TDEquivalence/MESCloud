package com.tde.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.util.Date;

@Entity
@Table(name = "counter_record")
@Getter
@Setter
public class CounterRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "equipment_output_id")
    private EquipmentOutputEntity equipmentOutput;
    private String equipmentOutputAlias;
    private int realValue;
    private int computedValue;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "production_order_id")
    private ProductionOrderEntity productionOrder;
    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;
}
