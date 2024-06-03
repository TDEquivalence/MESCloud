package com.alcegory.mescloud.model.entity;

import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy = "ims", fetch = FetchType.LAZY)
    private CountingEquipmentEntity countingEquipment;

    @OneToMany(mappedBy = "ims", fetch = FetchType.LAZY)
    private List<ProductionOrderEntity> productionOrders;

    private String code;

    public boolean isInUse() {
        return countingEquipment != null;
    }
}
