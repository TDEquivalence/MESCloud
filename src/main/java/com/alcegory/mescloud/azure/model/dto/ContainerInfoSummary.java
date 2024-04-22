package com.alcegory.mescloud.azure.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContainerInfoSummary {

    private String imageUrl;
    private String sasToken;
    private List<String> annotations;
    private String modelDecision;
}
