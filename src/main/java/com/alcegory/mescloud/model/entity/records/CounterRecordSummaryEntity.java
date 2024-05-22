package com.alcegory.mescloud.model.entity.records;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Entity(name = "counter_record_summary")
public class CounterRecordSummaryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipment_id")
    private Long equipmentId;

    @Column(name = "equipment_alias")
    private String equipmentAlias;

    @Column(name = "production_order_id")
    private Long productionOrderId;

    @Column(name = "production_order_code")
    private String productionOrderCode;

    @Column(name = "equipment_output_id")
    private Long equipmentOutputId;

    @Column(name = "equipment_output_alias")
    private String equipmentOutputAlias;

    @Column(name = "increment_day")
    private Long incrementDay;

    @Column(name = "active_time_day")
    private Long activeTimeDay;

    @Column(name = "registered_at")
    private Date registeredAt;

    @Column(name = "is_valid_for_production")
    private Boolean isValidForProduction;

    @Column(name = "ims")
    private String ims;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;

    @Column(name = "completed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp completedAt;
}
