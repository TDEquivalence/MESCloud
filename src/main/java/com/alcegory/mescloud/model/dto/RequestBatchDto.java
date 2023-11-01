package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestBatchDto {

    private Long composedId;
    private BatchDto batch;
}
