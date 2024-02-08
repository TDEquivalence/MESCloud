package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface ExportExcelService {

    List<ProductionOrderSummaryEntity> exportProductionOrderViewToExcel(HttpServletResponse response);

    void setExcelResponseHeaders(HttpServletResponse response, String filename);

    List<ComposedSummaryEntity> exportComposedWithoutHitsToExcel(HttpServletResponse response, boolean withHits);
}
