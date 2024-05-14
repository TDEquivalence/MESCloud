package com.alcegory.mescloud.model.entity.production;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "template_field_mapping")
public class TemplateFieldMappingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private ProductionOrderTemplateEntity template;

    @Column(name = "field_name")
    private String fieldName;
}
