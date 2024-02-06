package com.alcegory.mescloud.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductionOrderView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String countingEquipmentAlias;
    private String productionOrderCode;
    private String imsCode;
    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
    private Date createdAt;
    private Date completedAt;
    private long validAmount;
}
