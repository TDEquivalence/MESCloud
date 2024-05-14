package com.alcegory.mescloud.model.entity.records;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity(name = "counter_record_view")
public class CounterRecordSummaryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "equipment_alias")
    private String equipmentAlias;

    @Column(name = "production_order_code")
    private String productionOrderCode;

    @Column(name = "equipment_output_id")
    private Long equipmentOutputId;

    @Column(name = "equipment_output_alias")
    private String equipmentOutputAlias;

    @Column(name = "computed_value")
    private int computedValue;

    @Column(name = "registered_at")
    private Date registeredAt;

    @Column(name = "is_valid_for_production")
    private boolean isValidForProduction;

    @Column(name = "ims")
    private String ims;
}
