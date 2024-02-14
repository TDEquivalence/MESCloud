package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.KpiFilterDto;
import jakarta.servlet.http.HttpServletResponse;

public interface ExportExcelService {

    void exportProductionOrderViewToExcel(HttpServletResponse response, KpiFilterDto filter);

    void exportComposedToExcel(HttpServletResponse response, boolean withHits, KpiFilterDto filter);

    void exportCompletedComposedToExcel(HttpServletResponse response, boolean withHits, KpiFilterDto filter);
}
