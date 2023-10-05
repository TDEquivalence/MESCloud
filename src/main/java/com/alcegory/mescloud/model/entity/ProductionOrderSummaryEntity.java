package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "production_order_summary")
public class ProductionOrderSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private CountingEquipmentEntity equipment;
    @ManyToOne(fetch = FetchType.EAGER)
    private ImsEntity ims;
    @ManyToOne
    private ComposedProductionOrderEntity composedProductionOrder;

    private String code;
    private Integer targetAmount;
    private Boolean isEquipmentEnabled;
    private Boolean isCompleted;
    private Date createdAt;
    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;

    private Long validAmount;
}
