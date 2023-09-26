package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KpiAvailabilityDto {

    private double scheduledTime;
    private double effectiveTime;
    private double availabilityPercentage;
}
