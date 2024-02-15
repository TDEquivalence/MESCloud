package com.alcegory.mescloud.model.request;

import com.alcegory.mescloud.model.dto.EquipmentOutputDto;
import com.alcegory.mescloud.model.dto.ImsDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestConfigurationDto {

    private String alias;

    @JsonProperty("pTimerCommunicationCycle")
    private int pTimerCommunicationCycle;

    private List<EquipmentOutputDto> outputs;
    private ImsDto ims;

    private Double theoreticalProduction;
    private Double qualityTarget;
    private Double availabilityTarget;
    private Double performanceTarget;
    private Double overallEquipmentEffectivenessTarget;
}
