package com.alcegory.mescloud.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KpiDto {

    private Double dividend;
    private Double divider;
    private Double value;

    public KpiDto(Integer dividend, Integer divider) {
        this.dividend = Double.valueOf(dividend);
        this.divider = Double.valueOf(divider);
        this.value = this.dividend / this.divider;
    }

    public KpiDto(Double dividend, Double divider) {
        this.dividend = dividend;
        this.divider = divider;
        this.value = this.dividend / this.divider;
    }
}
