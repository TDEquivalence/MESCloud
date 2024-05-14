package com.alcegory.mescloud.model.dto.company;

import com.alcegory.mescloud.model.dto.company.FactoryDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyDto {

    private Long id;
    private String name;
    private List<FactoryDto> factoryList;
}
