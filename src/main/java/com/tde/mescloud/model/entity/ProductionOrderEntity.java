package com.tde.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Proxy;

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
    private int targetAmount;
    private boolean isCompleted;
    private Date createdAt;
    @OneToMany(mappedBy = "productionOrder")
    private List<ProductionInstructionEntity> productionInstructions;
    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
}
