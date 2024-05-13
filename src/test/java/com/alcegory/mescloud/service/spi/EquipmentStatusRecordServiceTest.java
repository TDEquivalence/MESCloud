package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.constant.EquipmentStatus;
import com.alcegory.mescloud.model.entity.EquipmentStatusRecordEntity;
import com.alcegory.mescloud.repository.equipment.EquipmentStatusRecordRepository;
import com.alcegory.mescloud.service.equipment.EquipmentStatusRecordServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
//@ExtendWith(MockitoExtension.class) - JUnit5 annotation
@RunWith(MockitoJUnitRunner.class)
public class EquipmentStatusRecordServiceTest {

    @Mock
    private EquipmentStatusRecordRepository equipmentStatusRecordRepository;
    @InjectMocks
    private EquipmentStatusRecordServiceImpl equipmentStatusRecordService;

    private EquipmentStatusRecordEntity createEquipmentStatusRecord(EquipmentStatus status, String createdAt) {
        EquipmentStatusRecordEntity record = new EquipmentStatusRecordEntity();
        record.setEquipmentStatus(status);
        record.setRegisteredAt(Timestamp.valueOf(createdAt));
        return record;
    }

    @Test
    public void testCalculateStoppageTimeInSeconds() {
        List<EquipmentStatusRecordEntity> equipmentStatusRecords = new ArrayList<>();
        equipmentStatusRecords.add(createEquipmentStatusRecord(EquipmentStatus.STOPPED, "2023-09-25 13:40:00"));
        equipmentStatusRecords.add(createEquipmentStatusRecord(EquipmentStatus.ACTIVE, "2023-09-25 13:55:00"));

        Timestamp startDate = Timestamp.valueOf("2023-09-25 13:50:00");
        Timestamp endDate = Timestamp.valueOf("2023-09-25 16:50:00");
        long equipmentId = 1;

        Mockito.when(equipmentStatusRecordRepository.findRecordsForPeriodAndLastBefore(equipmentId, startDate, endDate))
                .thenReturn(equipmentStatusRecords);

        Long result = equipmentStatusRecordService.calculateStoppageTimeInSeconds(equipmentId, startDate, endDate);

        // Expected stoppage time calculation:
        // 1. From 13:50:00 to 13:55:00 -> stopped for 300 seconds
        Assertions.assertEquals(300L, result.longValue());
    }
}
