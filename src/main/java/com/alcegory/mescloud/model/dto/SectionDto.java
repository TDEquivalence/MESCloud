package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SectionDto {

    private Long id;
    private String name;
    private List<CountingEquipmentDto> countingEquipments;
    private SectionConfigDto sectionConfigEntity;
}
