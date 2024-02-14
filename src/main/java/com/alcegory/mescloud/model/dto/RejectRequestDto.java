package com.alcegory.mescloud.model.dto;

import com.alcegory.mescloud.model.request.RequestBatchDto;
import com.alcegory.mescloud.model.request.RequestComposedDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectRequestDto {

    private RequestComposedDto productionOrderIds;
    private RequestBatchDto requestBatchDto;
}
