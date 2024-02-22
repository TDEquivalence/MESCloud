package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.FilterDto;
import jakarta.servlet.http.HttpServletResponse;

public interface ExportExcelService {


    void exportProductionOrderViewToExcelFiltered(HttpServletResponse response, FilterDto filter);


    void exportCompletedComposedToExcelFiltered(HttpServletResponse response, boolean withHits, FilterDto filter);

    void exportAllProductionAndComposedToExcel(HttpServletResponse response);

    void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, FilterDto filter);
}
