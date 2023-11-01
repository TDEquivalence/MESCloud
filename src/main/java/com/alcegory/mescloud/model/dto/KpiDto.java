package com.alcegory.mescloud.model.dto;

import com.alcegory.mescloud.utility.DoubleUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter
@NoArgsConstructor
@Log
public class KpiDto {

    private Double dividend;
    private Double divider;
    private Double value;

    public KpiDto(Integer dividend, Integer divider) {

        this.dividend = DoubleUtil.safeDoubleValue(dividend);
        this.divider = DoubleUtil.safeDoubleValue(divider);
    }

    public KpiDto(Double dividend, Double divider) {
        this.dividend = dividend;
        this.divider = divider;
    }

    public void setValueAsDivision() {
        if (hasInvalidDivisionMembers()) {
            log.warning(String.format("Unable to calculate value: cannot divide dividend [%s] by the divisor [%s]", dividend, divider));
            this.value = null;
            return;
        }

        this.value = this.dividend / this.divider;
    }

    public void setValueAsDifference() {
        if (hasInvalidSubtractionMembers()) {
            log.warning(String.format("Unable to calculate value: cannot subtract [%s] from [%s]", divider, dividend));
            this.value = null;
            return;
        }

        this.value = this.dividend - this.divider;
    }

    private boolean hasInvalidDivisionMembers() {
        return dividend == null || dividend == 0 || divider == null || divider == 0;
    }

    private boolean hasInvalidSubtractionMembers() {
        return dividend == null || dividend == 0 || divider == null;
    }
}
