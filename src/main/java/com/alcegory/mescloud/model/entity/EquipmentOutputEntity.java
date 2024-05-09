package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity(name = "equipment_output")
@Getter
@Setter
public class EquipmentOutputEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counting_equipment_id")
    private CountingEquipmentEntity countingEquipment;

    private String code;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "equipment_output_alias_id")
    private EquipmentOutputAliasEntity alias;

    private boolean isValidForProduction;
}