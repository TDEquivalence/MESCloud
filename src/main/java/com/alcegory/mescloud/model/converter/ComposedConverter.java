package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.composed.ComposedInfoDto;
import com.alcegory.mescloud.model.dto.composed.ExportComposedDto;
import com.alcegory.mescloud.model.entity.composed.ComposedSummaryEntity;

import java.util.List;

public interface ComposedConverter {

    ComposedInfoDto convertToDto(ComposedSummaryEntity entity);

    List<ComposedInfoDto> convertToDtoList(List<ComposedSummaryEntity> entities);

    List<ExportComposedDto> convertToExportDtoList(List<ComposedSummaryEntity> entities);
}
