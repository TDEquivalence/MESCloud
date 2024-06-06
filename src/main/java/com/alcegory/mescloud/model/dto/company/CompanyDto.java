package com.alcegory.mescloud.model.dto.company;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyDto {

    private Long id;
    private String name;
    private String prefix;
    private List<FactoryDto> factoryList;
}
