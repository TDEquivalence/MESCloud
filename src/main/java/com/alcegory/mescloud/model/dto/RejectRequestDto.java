package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectRequestDto {

    private RequestComposedDto productionOrderIds;
    private RequestBatchDto requestBatchDto;
}
