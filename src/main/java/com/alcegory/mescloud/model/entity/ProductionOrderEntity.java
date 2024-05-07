package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity(name = "production_order")
public class ProductionOrderEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @ManyToOne(fetch = FetchType.EAGER)
    private CountingEquipmentEntity equipment;
    @ManyToOne(fetch = FetchType.EAGER)
    private ImsEntity ims;
    private int targetAmount;
    private boolean isCompleted;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;

    @OneToMany(mappedBy = "productionOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductionInstructionEntity> productionInstructions;

    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
    private Boolean isApproved;

    @ManyToOne
    private ComposedProductionOrderEntity composedProductionOrder;

    public Map<String, String> getProductionInstructionsMap() {
        if (productionInstructions == null || productionInstructions.isEmpty()) {
            return Collections.emptyMap();
        }

        return productionInstructions.stream()
                .collect(Collectors.toMap(ProductionInstructionEntity::getName,
                        ProductionInstructionEntity::getValue,
                        (existingValue, newValue) -> existingValue,
                        HashMap::new));
    }
}


