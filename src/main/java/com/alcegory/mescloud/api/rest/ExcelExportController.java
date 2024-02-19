package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.KpiFilterDto;
import com.alcegory.mescloud.service.ExportExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void exportCompletedComposedAndProductionToExcel(HttpServletResponse response, KpiFilterDto filter) {
        exportExcelService.exportProductionAndComposedToExcelFiltered(response, filter);
    }
}