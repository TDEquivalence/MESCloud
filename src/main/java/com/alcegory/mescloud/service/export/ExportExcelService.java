package com.alcegory.mescloud.service.export;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface ExportExcelService {

    void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, Map<String, String> requestPayload);
}
