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
    @OneToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = false
    )
    @JoinColumn(name = "ims_id", referencedColumnName = "id")
    private ImsEntity ims;

    @OneToMany(
            mappedBy = "countingEquipment",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = false
    )
    private List<EquipmentOutputEntity> outputs;

    @OneToMany(mappedBy = "equipment", fetch = FetchType.LAZY)
    List<ProductionOrderEntity> productionOrders;

    @OneToMany(mappedBy = "countingEquipment", fetch = FetchType.LAZY)
    List<EquipmentStatusRecordEntity> equipmentStatusRecords;

    private Integer theoreticalProduction;

    private Double equipmentOverallEffectivenessTarget;
    private Double availabilityTarget;
    private Double performanceTarget;
    private Double qualityTarget;
}