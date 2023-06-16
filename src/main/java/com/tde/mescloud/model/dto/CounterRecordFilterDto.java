package com.tde.mescloud.model.dto;

import lombok.Data;

@Data
public class CounterRecordFilterDto {

    private int take;//10
    private int skip;//20
    private CounterRecordSearchDto[] search;
    private CounterRecordSortDto[] sort;
}
