package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "equipment_output")
@Getter
@Setter
public class EquipmentOutputEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private CountingEquipmentEntity countingEquipment;
    private String code;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "equipment_output_alias_id")
    private EquipmentOutputAliasEntity alias;

    private boolean isValidForProduction;
}