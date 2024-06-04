package com.alcegory.mescloud.model.entity.company;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity(name = "company")
public class CompanyEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String prefix;
    @OneToMany(mappedBy = "company", fetch = FetchType.EAGER)
    private List<FactoryEntity> factoryList;
}
