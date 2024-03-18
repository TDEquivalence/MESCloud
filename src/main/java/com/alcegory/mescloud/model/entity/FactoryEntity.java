package com.alcegory.mescloud.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "factory")
public class FactoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;
    @OneToMany(mappedBy = "factory", fetch = FetchType.EAGER)
    private List<SectionEntity> sectionList;
}


