package com.tde.mescloud.model.dto;

import lombok.Data;

@Data
public class CounterRecordFilterDto {

    private int take;
    private int skip;
    private CounterRecordSearchDto[] search;
    private CounterRecordSortDto[] sort;
}
