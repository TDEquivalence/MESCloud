package com.alcegory.mescloud.service;

import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.service.spi.ProductionOrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
//@ExtendWith(MockitoExtension.class) - JUnit5 annotation
@RunWith(MockitoJUnitRunner.class)
class ProductionOrderServiceTest {

    @Mock
    private ProductionOrderRepository productionOrderRepository;
    @InjectMocks
    private ProductionOrderServiceImpl productionOrderService;

    @Test
    void testFindExistingIds() {

        ProductionOrderEntity firstProductionOrderEntity = new ProductionOrderEntity();
        firstProductionOrderEntity.setId(1L);
        ProductionOrderEntity secondProductionOrderEntity = new ProductionOrderEntity();
        secondProductionOrderEntity.setId(2L);
        ProductionOrderEntity thirdProductionOrderEntity = new ProductionOrderEntity();
        thirdProductionOrderEntity.setId(3L);

        List<Long> inputIds = Arrays.asList(1L, 2L, 3L);
        List<ProductionOrderEntity> mockEntities =
                Arrays.asList(firstProductionOrderEntity, secondProductionOrderEntity, thirdProductionOrderEntity);


        when(productionOrderRepository.findByIdIn(inputIds)).thenReturn(mockEntities);
        List<Long> result = productionOrderService.findExistingIds(inputIds);

        assertEquals(inputIds, result);
    }

    @Test
    void testFindExistingIdsWithExclude() {

        ProductionOrderEntity firstProductionOrderEntity = new ProductionOrderEntity();
        firstProductionOrderEntity.setId(1L);
        ProductionOrderEntity secondProductionOrderEntity = new ProductionOrderEntity();
        secondProductionOrderEntity.setId(2L);

        List<Long> inputIds = Arrays.asList(1L, 2L, 3L);
        List<ProductionOrderEntity> mockEntities =
                Arrays.asList(firstProductionOrderEntity, secondProductionOrderEntity);

        List<Long> expectedIds = Arrays.asList(1L, 2L);

        when(productionOrderRepository.findByIdIn(inputIds)).thenReturn(mockEntities);
        List<Long> result = productionOrderService.findExistingIds(inputIds);

        assertEquals(expectedIds, result);
    }

    @Test
    void testCalculateActiveTime() {

        Long equipmentId = 1L;
        Date startDate = new Date();
        Date endDate = new Date(System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000)); // 3 days later

        ProductionOrderEntity order1 = new ProductionOrderEntity();
        order1.setCreatedAt(new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)); // 2 days before startDate
        order1.setCompletedAt(new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000));  // 1 day before endDate

        ProductionOrderEntity order2 = new ProductionOrderEntity();
        order2.setCreatedAt(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));  // 1 day before startDate
        order2.setCompletedAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)); // 1 day after Order 1

        List<ProductionOrderEntity> productionOrders = new ArrayList<>();
        productionOrders.add(order1);
        productionOrders.add(order2);

        when(productionOrderRepository.findByEquipmentAndPeriod(equipmentId, startDate, endDate))
                .thenReturn(productionOrders);

        Long activeTime = productionOrderService.calculateScheduledTimeInSeconds(equipmentId, startDate, endDate);

        long expectedActiveTimeSeconds = 24 * 60 * 60; // 1 day (Order 1) + 1 day (Order 2)
        assertEquals(expectedActiveTimeSeconds, activeTime);
    }

    @Test
    void testGenerateCodeWithNullCode() {
        ProductionOrderEntity productionOrder = new ProductionOrderEntity();
        when(productionOrderRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(productionOrder));

        Exception exception = assertThrows(IllegalStateException.class, () -> productionOrderService.generateCode());
        assertEquals("Unable to generate new code: last stored Production Order code is null or empty", exception.getMessage());
    }

    @Test
    void testGenerateCodeWithProperlyFormattedCode() {
        ProductionOrderEntity productionOrderEntity = new ProductionOrderEntity();
        productionOrderEntity.setCode("OBOPO2300001");
        when(productionOrderRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(productionOrderEntity));

        String generatedCode = productionOrderService.generateCode();
        assertEquals("OBOPO2300002", generatedCode);
    }
}
