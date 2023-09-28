package com.alcegory.mescloud.model.dto;

import com.alcegory.mescloud.model.entity.SectionEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FactoryDto {

    private Long id;
    private String name;
    private List<SectionEntity> sections;
}
