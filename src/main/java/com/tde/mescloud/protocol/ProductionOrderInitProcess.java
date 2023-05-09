package com.tde.mescloud.protocol;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.dto.CounterMqttDTO;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDTO;
import com.tde.mescloud.service.CounterRecordService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Log
public class ProductionOrderInitProcess extends AbstractMesProtocolProcess<EquipmentCountsMqttDTO> {

    private final int INITIAL_COMPUTED_VALUE = 0;
    private final CounterRecordService counterRecordService;
    private final CounterRecordConverter counterRecordConverter;

    public ProductionOrderInitProcess(CounterRecordService counterRecordService,
                                      CounterRecordConverter counterRecordConverter) {
        this.counterRecordService = counterRecordService;
        this.counterRecordConverter = counterRecordConverter;
    }

    @Override
    public void execute(EquipmentCountsMqttDTO equipmentCountsDTO) {
        log.info("Executing Production Order init process");
        List<CounterRecord> counterRecords = new ArrayList<>(equipmentCountsDTO.getCounters().length);
        for (CounterMqttDTO counterDTO : equipmentCountsDTO.getCounters()) {
            CounterRecord counterRecord = counterRecordConverter.convertToDO(equipmentCountsDTO, counterDTO);
            counterRecord.setComputedValue(INITIAL_COMPUTED_VALUE);
            counterRecord.setRegisteredAt(new Date());
            counterRecords.add(counterRecord);
        }
        counterRecordService.save(counterRecords);
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.PRODUCTION_ORDER_INIT_DTO_NAME;
    }
}
