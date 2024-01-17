package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "batch")
public class BatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @OneToOne
    @JoinColumn(name = "composed_production_order_id")
    private ComposedProductionOrderEntity composedProductionOrder;

    private Boolean isApproved;
}
