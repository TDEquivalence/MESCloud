package com.tde.mescloud.model.dto;

import lombok.Data;

@Data
public class CounterRecordFilterDto {

    private int take;
    private int skip;
    private CounterRecordSearchDto[] search;
    //String id -> startDate, equipmentName
    //String value; 22/04/2023, Máquina 1
    private CounterRecordSortDto[] sort;
}