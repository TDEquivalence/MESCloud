package com.alcegory.mescloud.model.request;

import com.alcegory.mescloud.model.dto.BatchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestBatchDto {

    private Long composedId;
    private BatchDto batch;
}
