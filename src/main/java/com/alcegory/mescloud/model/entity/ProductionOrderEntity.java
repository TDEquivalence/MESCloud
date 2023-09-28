package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity(name = "production_order")
@Getter
@Setter
public class ProductionOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @ManyToOne(fetch = FetchType.EAGER)
    private CountingEquipmentEntity equipment;
    @ManyToOne(fetch = FetchType.EAGER)
    private ImsEntity ims;
    private int targetAmount;
    private boolean isCompleted;
    private Date createdAt;
    private Date completedAt;

    @OneToMany(mappedBy = "productionOrder")
    private List<ProductionInstructionEntity> productionInstructions;
    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
    private Boolean isApproved;

    @ManyToOne
    private ComposedProductionOrderEntity composedProductionOrder;
}
