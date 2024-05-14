package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.ImsDto;
import com.alcegory.mescloud.model.entity.ImsEntity;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GenericConverterTest {

    private GenericConverterImpl<ImsEntity, ImsDto> converter;

    @BeforeEach
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        converter = new GenericConverterImpl<>(modelMapper);
    }

    @Test
    void testToEntity() {
        ImsDto dto = new ImsDto();
        dto.setId(1L);
        dto.setCode("ABC");
        dto.setCountingEquipmentId(42L);

        ImsEntity entity = converter.toEntity(dto, ImsEntity.class);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getCode(), entity.getCode());
        assertEquals(dto.getCountingEquipmentId(), entity.getCountingEquipment().getId());
    }

    @Test
    void testToDto() {
        ImsEntity entity = new ImsEntity();
        entity.setId(2L);
        entity.setCode("XYZ");
        CountingEquipmentEntity countingEquipment = new CountingEquipmentEntity();
        countingEquipment.setId(99L);
        entity.setCountingEquipment(countingEquipment);

        ImsDto dto = converter.toDto(entity, ImsDto.class);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getCode(), dto.getCode());
        assertEquals(entity.getCountingEquipment().getId(), dto.getCountingEquipmentId());
    }

    @Test
    void testToDtoList() {
        List<ImsEntity> entityList = createImsEntityList();
        List<ImsDto> dtoList = converter.toDto(entityList, ImsDto.class);

        assertEquals(entityList.size(), dtoList.size());
        assertTrue(dtoList.stream().allMatch(Objects::nonNull));

        for (int i = 0; i < entityList.size(); i++) {
            assertEquals(entityList.get(i).getId(), dtoList.get(i).getId());
            assertEquals(entityList.get(i).getCode(), dtoList.get(i).getCode());
            assertEquals(entityList.get(i).getCountingEquipment().getId(), dtoList.get(i).getCountingEquipmentId());
        }
    }

    private List<ImsEntity> createImsEntityList() {
        List<ImsEntity> entityList = new ArrayList<>();

        ImsEntity entity1 = new ImsEntity();
        entity1.setId(1L);
        entity1.setCode("Entity 1");
        CountingEquipmentEntity countingEquipment = new CountingEquipmentEntity();
        countingEquipment.setId(10L);
        entity1.setCountingEquipment(countingEquipment);

        ImsEntity entity2 = new ImsEntity();
        entity2.setId(2L);
        entity2.setCode("Entity 2");
        CountingEquipmentEntity countingEquipment2 = new CountingEquipmentEntity();
        countingEquipment2.setId(20L);
        entity2.setCountingEquipment(countingEquipment2);

        ImsEntity entity3 = new ImsEntity();
        entity3.setId(3L);
        entity3.setCode("Entity 3");
        CountingEquipmentEntity countingEquipment3 = new CountingEquipmentEntity();
        countingEquipment3.setId(30L);
        entity3.setCountingEquipment(countingEquipment3);

        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);

        return entityList;
    }


    @Test
    void testToEntityList() {
        List<ImsDto> dtoList = createImsDtoList();
        List<ImsEntity> entityList = converter.toEntity(dtoList, ImsEntity.class);

        assertEquals(dtoList.size(), entityList.size());

        for (int i = 0; i < dtoList.size(); i++) {
            assertEquals(dtoList.get(i).getId(), entityList.get(i).getId());
            assertEquals(dtoList.get(i).getCode(), entityList.get(i).getCode());
            assertEquals(dtoList.get(i).getCountingEquipmentId(), entityList.get(i).getCountingEquipment().getId());
        }
    }

    List<ImsDto> createImsDtoList() {
        List<ImsDto> dtoList = new ArrayList<>();

        ImsDto dto1 = new ImsDto();
        dto1.setId(101L);
        dto1.setCode("DTO 1");
        dto1.setCountingEquipmentId(111L);

        ImsDto dto2 = new ImsDto();
        dto2.setId(202L);
        dto2.setCode("DTO 2");
        dto2.setCountingEquipmentId(222L);

        ImsDto dto3 = new ImsDto();
        dto3.setId(303L);
        dto3.setCode("DTO 3");
        dto3.setCountingEquipmentId(333L);

        dtoList.add(dto1);
        dtoList.add(dto2);
        dtoList.add(dto3);

        return dtoList;
    }
}