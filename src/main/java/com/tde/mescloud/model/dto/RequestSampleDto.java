package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestSampleDto {

    private List<Long> productionOrdersIds;
    private int amount;
}
