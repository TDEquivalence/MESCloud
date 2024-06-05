package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.dto.mqqt.CounterMqttDto;
import com.alcegory.mescloud.model.dto.mqqt.PlcMqttDto;
import com.alcegory.mescloud.repository.record.CounterRecordRepository;
import com.alcegory.mescloud.service.equipment.CountingEquipmentServiceImpl;
import com.alcegory.mescloud.service.equipment.EquipmentOutputServiceImpl;
import com.alcegory.mescloud.service.record.CounterRecordServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
}

