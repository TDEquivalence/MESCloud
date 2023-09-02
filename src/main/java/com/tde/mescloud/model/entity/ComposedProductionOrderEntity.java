package com.tde.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity(name = "composed_production_order")
@Getter
@Setter
public class ComposedProductionOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @OneToMany(mappedBy = "composedProductionOrder", cascade = CascadeType.MERGE)
    private List<ProductionOrderEntity> productionOrders = new ArrayList<>();
}
