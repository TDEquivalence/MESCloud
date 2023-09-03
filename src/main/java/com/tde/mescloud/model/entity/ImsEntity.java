package com.tde.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    @OneToMany(mappedBy = "ims", fetch = FetchType.LAZY)
    private List<ProductionOrderEntity> productionOrders;


    public boolean isAssociated() {
        return countingEquipment != null;
    }
}
