package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "section_config")
public class SectionConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_id")
    private SectionEntity section;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "section_config_feature",
            joinColumns = @JoinColumn(name = "section_config_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id"))
    private List<FeatureEntity> featureList;
}


