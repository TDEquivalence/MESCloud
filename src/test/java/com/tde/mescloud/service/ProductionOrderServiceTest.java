package com.tde.mescloud.service;

import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.repository.ProductionOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
//@ExtendWith(MockitoExtension.class)
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
}
