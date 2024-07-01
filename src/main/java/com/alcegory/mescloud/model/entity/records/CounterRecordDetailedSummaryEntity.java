package com.alcegory.mescloud.model.entity.records;

import com.alcegory.mescloud.model.converter.InstructionsConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity(name = "counter_record_detailed_summary")
public class CounterRecordDetailedSummaryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipment_id")
    private Long equipmentId;

    @Column(name = "equipment_alias")
    private String equipmentAlias;

    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "production_order_id")
    private Long productionOrderId;

    @Column(name = "production_order_code")
    private String productionOrderCode;

    @Column(name = "equipment_output_id")
    private Long equipmentOutputId;

    @Column(name = "equipment_output_alias")
    private String equipmentOutputAlias;

    @Column(name = "registered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;

    @Column(name = "is_valid_for_production")
    private Boolean isValidForProduction;

    @Column(name = "computed_value")
    private Long computedValue;

    @Column(name = "computed_active_time")
    private Long computedActiveTime;

    @Column(name = "ims")
    private String ims;

    @Convert(converter = InstructionsConverter.class)
    private List<Map<String, Object>> instructions;
}
