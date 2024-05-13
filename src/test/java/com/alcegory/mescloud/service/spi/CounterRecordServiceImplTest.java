package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.repository.record.CounterRecordRepository;
import com.alcegory.mescloud.service.equipment.CountingEquipmentServiceImpl;
import com.alcegory.mescloud.service.equipment.EquipmentOutputServiceImpl;
import com.alcegory.mescloud.service.record.CounterRecordServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class CounterRecordServiceImplTest {

    @Mock
    private CounterRecordRepository repository;

    @Mock
    private CountingEquipmentServiceImpl countingEquipmentService;

    @Mock
    private EquipmentOutputServiceImpl equipmentOutputService;

    @InjectMocks
    private CounterRecordServiceImpl service;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessCounterRecord_InvalidEquipmentCounts() {
        PlcMqttDto invalidEquipmentCounts = new PlcMqttDto();
        invalidEquipmentCounts.setEquipmentCode("InvalidCode");
        invalidEquipmentCounts.setCounters(new CounterMqttDto[0]);

        when(countingEquipmentService.findByCode("InvalidCode")).thenReturn(Optional.empty());
        service.processCounterRecord(invalidEquipmentCounts);

        verify(repository, never()).saveAll(anyList());
    }

    @Test
    public void testProcessCounterRecord_ValidEquipmentCounts() {
        // Mocking valid equipment counts
        PlcMqttDto validEquipmentCounts = new PlcMqttDto();
        validEquipmentCounts.setEquipmentCode("ValidCode");
        CounterMqttDto counterMqttDto = new CounterMqttDto();
        counterMqttDto.setOutputCode("OutputCode");
        validEquipmentCounts.setCounters(new CounterMqttDto[]{counterMqttDto});


        EquipmentOutputDto equipmentOutputDto = new EquipmentOutputDto();
        EquipmentOutputAliasDto aliasDto = new EquipmentOutputAliasDto();
        aliasDto.setAlias("TestAlias");
        equipmentOutputDto.setAlias(aliasDto);

        CountingEquipmentDto countingEquipmentDto = new CountingEquipmentDto();
        countingEquipmentDto.setOutputs(List.of(equipmentOutputDto));

        when(countingEquipmentService.findByCode("ValidCode")).thenReturn(Optional.of(countingEquipmentDto));
        when(equipmentOutputService.findByCode("OutputCode")).thenReturn(Optional.of(equipmentOutputDto));

        service.processCounterRecord(validEquipmentCounts);

        verify(repository).saveAll(ArgumentMatchers.argThat(argument -> {
            // Check if the argument is a list with size 1
            return ((List<?>) argument).size() == 1;
        }));
    }

}

