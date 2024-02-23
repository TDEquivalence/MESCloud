package com.alcegory.mescloud.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestToRejectBatchDto {

    private RequestComposedDto productionOrderIds;
    private RequestBatchDto requestBatchDto;
}
