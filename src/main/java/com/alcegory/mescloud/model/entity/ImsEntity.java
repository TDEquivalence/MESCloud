package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity(name = "ims")
public class ImsEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    @OneToOne(mappedBy = "ims")
    CountingEquipmentEntity countingEquipment;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @OneToMany(mappedBy = "ims", fetch = FetchType.LAZY)
    private List<ProductionOrderEntity> productionOrders;


    public boolean isInUse() {
        return countingEquipment != null;
    }
}
