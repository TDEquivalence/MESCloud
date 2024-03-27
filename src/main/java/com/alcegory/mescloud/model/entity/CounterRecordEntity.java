package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "counter_record")
@Getter
@Setter
@NoArgsConstructor
public class CounterRecordEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_output_id")
    private EquipmentOutputEntity equipmentOutput;
    private String equipmentOutputAlias;
    private int realValue;
    private int computedValue;
    private Integer increment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id")
    private ProductionOrderEntity productionOrder;
    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;
    private Boolean isValidForProduction;
    private int activeTime;
    private int computedActiveTime;
    private Integer incrementActiveTime;

    public CounterRecordEntity(Long id,
                               EquipmentOutputEntity equipmentOutput,
                               String equipmentOutputAlias,
                               int computedValue,
                               ProductionOrderEntity productionOrder,
                               Date registeredAt,
                               Boolean isValidForProduction
    ) {
        this.id = id;
        this.equipmentOutput = equipmentOutput;
        this.equipmentOutputAlias = equipmentOutputAlias;
        this.computedValue = computedValue;
        this.productionOrder = productionOrder;
        this.registeredAt = registeredAt;
        this.isValidForProduction = isValidForProduction;
    }
}