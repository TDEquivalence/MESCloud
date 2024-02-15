package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.KpiFilterDto;
import jakarta.servlet.http.HttpServletResponse;

public interface ExportExcelService {

    void exportAllProductionOrderViewToExcel(HttpServletResponse response);

    void exportProductionOrderViewToExcelFiltered(HttpServletResponse response, KpiFilterDto filter);

    void exportAllComposedToExcel(HttpServletResponse response, boolean withHits);

    void exportComposedToExcelFiltered(HttpServletResponse response, boolean withHits, KpiFilterDto filter);

    void exportAllCompletedComposedToExcel(HttpServletResponse response, boolean withHits);

    void exportCompletedComposedToExcelFiltered(HttpServletResponse response, boolean withHits, KpiFilterDto filter);

    void exportAllProductionAndComposedToExcel(HttpServletResponse response);

    void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, KpiFilterDto filter);
}
