package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "production_order_summary")
public class ProductionOrderSummaryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
    private Date completedAt;
    private Long validAmount;
}
