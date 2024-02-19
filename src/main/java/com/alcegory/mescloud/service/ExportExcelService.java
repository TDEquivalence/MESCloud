package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.KpiFilterDto;
import jakarta.servlet.http.HttpServletResponse;

public interface ExportExcelService {


    void exportProductionOrderViewToExcelFiltered(HttpServletResponse response, KpiFilterDto filter);


    void exportCompletedComposedToExcelFiltered(HttpServletResponse response, boolean withHits, KpiFilterDto filter);

    void exportAllProductionAndComposedToExcel(HttpServletResponse response);

    void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, KpiFilterDto filter);
}
