package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity(name = "composed_production_order")
@Getter
@Setter
public class ComposedProductionOrderEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvedAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date hitInsertedAt;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "composedProductionOrder", cascade = CascadeType.MERGE)
    private List<ProductionOrderEntity> productionOrders = new ArrayList<>();
}
