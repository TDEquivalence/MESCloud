package com.alcegory.mescloud.model.dto;

import com.alcegory.mescloud.security.model.SectionRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SectionDto {

    private Long id;
    private String name;
    private SectionRole sectionRole;
    private List<SectionConfigDto> sectionConfigList;
}
