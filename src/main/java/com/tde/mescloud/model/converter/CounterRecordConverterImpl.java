package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.entity.CounterRecordConclusionEntity;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@Log
@AllArgsConstructor
public class CounterRecordConverterImpl implements CounterRecordConverter {

    private final ModelMapper mapper;


    public CounterRecordDto toDto(CounterRecordEntity entity) {
        return mapper.map(entity, CounterRecordDto.class);
    }

    @Override
    public CounterRecordDto conclusionViewToDto(CounterRecordConclusionEntity entity) {
        return mapper.map(entity, CounterRecordDto.class);
    }
}
