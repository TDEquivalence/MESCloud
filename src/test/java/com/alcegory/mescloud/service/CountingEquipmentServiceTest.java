package com.alcegory.mescloud.service;

import com.alcegory.mescloud.exception.ActiveProductionOrderException;
import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.repository.CountingEquipmentRepository;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.EquipmentOutputDto;
import com.alcegory.mescloud.model.dto.ImsDto;
import com.alcegory.mescloud.model.dto.RequestConfigurationDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CountingEquipmentServiceTest {

    @InjectMocks
    private CountingEquipmentServiceImpl countingEquipmentService;

    @Mock
    private CountingEquipmentRepository repository;

    @Mock
    private ProductionOrderServiceImpl productionOrderService;

    @Mock
    private GenericConverter<CountingEquipmentEntity, CountingEquipmentDto> converter;


    @Test
    public void testUpdateConfiguration_NullPropertyOnRequest() {
        long equipmentId = 10L;
        RequestConfigurationDto request = new RequestConfigurationDto();

        try {
            countingEquipmentService.updateConfiguration(equipmentId, request);
        } catch (IncompleteConfigurationException | EmptyResultDataAccessException | ActiveProductionOrderException e) {
            assertTrue(e instanceof IncompleteConfigurationException);
        }

        verify(repository, never()).save(any());
    }

    @Test
    public void testUpdateConfiguration_NoEquipmentFound() {
        long equipmentId = 10L;
        RequestConfigurationDto request = createFilledRequestConfigurationDto();

        try {
            countingEquipmentService.updateConfiguration(equipmentId, request);
        } catch (IncompleteConfigurationException | EmptyResultDataAccessException | ActiveProductionOrderException e) {
            assertTrue(e instanceof EmptyResultDataAccessException);
        }

        verify(repository, times(1)).findByIdWithActiveProductionOrder(equipmentId);
        verify(repository, never()).save(any());
    }

    public RequestConfigurationDto createFilledRequestConfigurationDto() {
        RequestConfigurationDto request = new RequestConfigurationDto();
        request.setAlias("SampleAlias");
        request.setPTimerCommunicationCycle(10);
        List<EquipmentOutputDto> outputs = new ArrayList<>();
        EquipmentOutputDto output1 = new EquipmentOutputDto();
        outputs.add(output1);
        request.setOutputs(outputs);
        ImsDto imsDto = new ImsDto();
        request.setImsDto(imsDto);
        request.setEquipmentEffectiveness(0.95);
        request.setTheoreticalProduction(1000);
        request.setAvailabilityTarget(0.99);
        request.setPerformanceTarget(0.98);
        request.setQuality(0.97);

        return request;
    }
}