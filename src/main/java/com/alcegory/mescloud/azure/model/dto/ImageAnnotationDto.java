package com.alcegory.mescloud.azure.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String mesUserDecision;
    private String classification;
    @JsonProperty("user_approval")
    private boolean userApproval;
    private List<String> rejection;
    private String comments;
    @JsonProperty("log_decision")
    private List<String> logDecision;
    private String username;
}
