package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.production.ComposedInfoDto;
import com.alcegory.mescloud.model.dto.production.ExportComposedDto;
import com.alcegory.mescloud.model.entity.production.ComposedSummaryEntity;

import java.util.List;

public interface ComposedConverter {

    ComposedInfoDto convertToDto(ComposedSummaryEntity entity);

    List<ComposedInfoDto> convertToDtoList(List<ComposedSummaryEntity> entities);

    List<ExportComposedDto> convertToExportDtoList(List<ComposedSummaryEntity> entities);
}
