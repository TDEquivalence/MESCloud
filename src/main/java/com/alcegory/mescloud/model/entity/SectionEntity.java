package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "section")
public class SectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "section", fetch = FetchType.EAGER)
    private List<CountingEquipmentEntity> countingEquipments;
    @ManyToOne
    private FactoryEntity factory;
    @OneToMany(mappedBy = "section", fetch = FetchType.EAGER)
    private List<SectionConfigEntity> sectionConfigList;
}


