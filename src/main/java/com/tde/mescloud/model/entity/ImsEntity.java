package com.tde.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ims")
@Getter
@Setter
public class ImsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @OneToOne(mappedBy = "ims")
    CountingEquipmentEntity countingEquipment;
}
