package com.alcegory.mescloud.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ExportExcelService {

    void exportAllProductionOrderViewToExcel(HttpServletResponse response);

    void exportAllComposedToExcel(HttpServletResponse response, boolean withHits);

    void exportAllCompletedComposedToExcel(HttpServletResponse response, boolean withHits);
}
