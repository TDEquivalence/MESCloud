package com.alcegory.mescloud.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface ExportExcelService {


    void exportProductionOrderViewToExcelFiltered(HttpServletResponse response, @RequestBody Map<String, String> requestPayload);


    void exportCompletedComposedToExcelFiltered(HttpServletResponse response, boolean withHits, @RequestBody Map<String, String> requestPayload);

    void exportAllProductionAndComposedToExcel(HttpServletResponse response);

    void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, Map<String, String> requestPayload);
}
