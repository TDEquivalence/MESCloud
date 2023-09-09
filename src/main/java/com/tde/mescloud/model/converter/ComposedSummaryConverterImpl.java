package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.ComposedSummaryDto;
import com.tde.mescloud.model.entity.ComposedSummaryEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ComposedSummaryConverterImpl implements ComposedSummaryConverter {

    private ModelMapper modelMapper;

    @Override
    public ComposedSummaryDto toDto(ComposedSummaryEntity entity) {
        return modelMapper.map(entity, ComposedSummaryDto.class);
    }
}
