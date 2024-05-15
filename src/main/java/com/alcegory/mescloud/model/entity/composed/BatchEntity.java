package com.alcegory.mescloud.model.entity.composed;

import com.alcegory.mescloud.model.entity.composed.ComposedProductionOrderEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "batch")
public class BatchEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @OneToOne
    @JoinColumn(name = "composed_production_order_id")
    private ComposedProductionOrderEntity composedProductionOrder;

    private Boolean isApproved;
}
