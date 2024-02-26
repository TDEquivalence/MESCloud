package com.alcegory.mescloud.service;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface ExportExcelService {

    void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, Map<String, String> requestPayload);
}
