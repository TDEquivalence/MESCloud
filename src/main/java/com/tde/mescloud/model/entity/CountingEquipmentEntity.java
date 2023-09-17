package com.tde.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "counting_equipment")
@Getter
@Setter
public class CountingEquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String alias;
    @ManyToOne
    private SectionEntity section;
    private int equipmentStatus;
    private int pTimerCommunicationCycle;
    @OneToOne(orphanRemoval = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ims_id", referencedColumnName = "id")
    private ImsEntity ims;

    @OneToMany(mappedBy = "countingEquipment", fetch = FetchType.EAGER)
    List<EquipmentOutputEntity> outputs;
    @OneToMany(mappedBy = "equipment", fetch = FetchType.LAZY)
    List<ProductionOrderEntity> productionOrders;

    private Double equipmentEffectiveness;
    private Integer theoreticalProduction;
    private Double availability;
    private Double performance;
    private Double quality;
}