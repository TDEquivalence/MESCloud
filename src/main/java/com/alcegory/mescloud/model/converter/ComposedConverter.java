package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.composed.ComposedExportInfoDto;
import com.alcegory.mescloud.model.dto.composed.ExportComposedDto;
import com.alcegory.mescloud.model.entity.composed.ComposedSummaryEntity;

import java.util.List;

public interface ComposedConverter {

    ComposedExportInfoDto convertToDto(ComposedSummaryEntity entity);

    List<ComposedExportInfoDto> convertToDtoList(List<ComposedSummaryEntity> entities);

    List<ExportComposedDto> convertToExportDtoList(List<ComposedSummaryEntity> entities);
}
