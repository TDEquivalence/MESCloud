package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity(name = "production_instruction")
@Getter
@Setter
public class ProductionInstructionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int instruction;
    @ManyToOne
    private ProductionOrderEntity productionOrder;
    //TODO: Add User relationship once UserEntity is defined
    //private User createdBy;
    private Date createdAt;
}
