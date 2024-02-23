package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.service.ExportExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/export")
@AllArgsConstructor
public class ExcelExportController {

    private final ExportExcelService exportExcelService;

    @GetMapping("composed-and-production-orders/completed/all")
    public void exportAllCompletedComposedAndProductionToExcel(HttpServletResponse response) {
        exportExcelService.exportAllProductionAndComposedToExcel(response);
    }

    @PostMapping("composed-and-production-orders/completed")
    public void exportCompletedComposedAndProductionToExcel(HttpServletResponse response, @RequestBody Map<String, String> requestPayload) {
        exportExcelService.exportProductionAndComposedToExcelFiltered(response, requestPayload);
    }
}