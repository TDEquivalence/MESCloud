package com.alcegory.mescloud.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestSampleDto {

    private List<Long> productionOrderIds;
    private int amount;
}
