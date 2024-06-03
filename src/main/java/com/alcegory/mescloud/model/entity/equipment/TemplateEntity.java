package com.alcegory.mescloud.model.entity.equipment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity(name = "production_order_template")
public class TemplateEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String templateName;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TemplateFieldMappingEntity> fields;

    @JsonIgnore
    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY) // Relationship with CountingEquipmentEntity
    private List<CountingEquipmentEntity> countingEquipment;
}

