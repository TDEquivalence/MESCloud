package com.alcegory.mescloud.model.dto.equipment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TemplateDto {

    private String templateName;
    private List<TemplateFieldsDto> fields;
}