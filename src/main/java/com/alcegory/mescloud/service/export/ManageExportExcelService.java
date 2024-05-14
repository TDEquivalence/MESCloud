package com.alcegory.mescloud.service.export;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface ManageExportExcelService {

    void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, Map<String, String> requestPayload);
}
