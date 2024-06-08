package com.alcegory.mescloud.model.dto.company;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FactoryDto {

    private Long id;
    private String name;
    private List<SectionDto> sectionList;
}
