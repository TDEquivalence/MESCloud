package com.alcegory.mescloud.azure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageAnnotationDto {

    private DataDto data;
    private List<AnnotationDto> annotations;
    @JsonProperty("model_decision")
    private String modelDecision;
    @JsonProperty("user_decision")
    private String userDecision;
    private String classification;
    @JsonProperty("user_approval")
    private boolean userApproval;
    private List<String> rejection;
    private String comments;
}
