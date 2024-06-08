package com.alcegory.mescloud.model.dto.equipment;

import com.alcegory.mescloud.model.dto.company.FeatureDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CountingEquipmentSummaryDto {

    private long id;
    private long sectionId;
    private String code;
    private String alias;
    List<FeatureDto> features;
}