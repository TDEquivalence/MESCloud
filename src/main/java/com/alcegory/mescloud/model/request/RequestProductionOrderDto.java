package com.alcegory.mescloud.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestProductionOrderDto {

    private long id;
    private long composedId;
    private int targetAmount;

    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
}
