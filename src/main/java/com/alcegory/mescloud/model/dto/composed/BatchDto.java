package com.alcegory.mescloud.model.dto.composed;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchDto {

    private Long id;
    private String code;
    private Boolean isApproved;
}
