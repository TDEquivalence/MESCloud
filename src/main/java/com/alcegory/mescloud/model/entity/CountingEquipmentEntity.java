package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "counting_equipment")
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
            cascade = {CascadeType.PERSIST},
            orphanRemoval = false
    )
    @JoinColumn(name = "ims_id", referencedColumnName = "id")
    private ImsEntity ims;

    @OneToMany(
            mappedBy = "countingEquipment",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST},
            orphanRemoval = false
    )
    private List<EquipmentOutputEntity> outputs;

    @OneToMany(mappedBy = "equipment", fetch = FetchType.LAZY)
    List<ProductionOrderEntity> productionOrders;

    @OneToMany(mappedBy = "countingEquipment", fetch = FetchType.LAZY)
    List<EquipmentStatusRecordEntity> equipmentStatusRecords;

    private Double theoreticalProduction;

    private Double qualityTarget;
    private Double availabilityTarget;
    private Double performanceTarget;
    private Double overallEquipmentEffectivenessTarget;
    private int unrecognizedAlarmDuration;

    @Enumerated(EnumType.STRING)
    private OperationStatus operationStatus;

    @Getter
    public enum OperationStatus {
        PENDING("PENDING"),
        IN_PROGRESS("IN_PROGRESS"),
        IDLE("IDLE");

        private final String value;

        OperationStatus(String value) {
            this.value = value;
        }

    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "counting_equipment_feature",
            joinColumns = @JoinColumn(name = "counting_equipment_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id"))
    private List<FeatureEntity> features;
}