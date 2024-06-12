package com.alcegory.mescloud.service.export;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface ManageExportExcelService {

<<<<<<< HEAD
    void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, Map<String, String> requestPayload);
=======
    void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, Map<String, String> requestPayload,
                                                    long sectionId);
>>>>>>> test_environment
}
