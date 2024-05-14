package com.alcegory.mescloud.model.entity.records;

import com.alcegory.mescloud.model.converter.InstructionsConverter;
import com.alcegory.mescloud.model.entity.equipment.EquipmentOutputEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "counter_record_production_conclusion")
@Getter
@Setter
public class CounterRecordConclusionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_output_id")
    private EquipmentOutputEntity equipmentOutput;
    private String equipmentOutputAlias;
    private int realValue;
    private int computedValue;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id")
    private ProductionOrderEntity productionOrder;
    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;
    private Boolean isValidForProduction;

    @Convert(converter = InstructionsConverter.class)
    private List<Map<String, Object>> instructions;
}
