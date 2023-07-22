package com.tde.mescloud.model.entity;

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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_output_alias_id")
    private EquipmentOutputAliasEntity alias;
}
