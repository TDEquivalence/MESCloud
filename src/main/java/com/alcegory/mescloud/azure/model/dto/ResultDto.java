package com.alcegory.mescloud.azure.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultDto {

    @JsonProperty("from_name")
    private String fromName;
    @JsonProperty("to_name")
    private String toName;
    private String type;
    @JsonProperty("original_width")
    private Integer originalWidth;
    @JsonProperty("original_height")
    private Integer originalHeight;
    private ValueDto value;
}
