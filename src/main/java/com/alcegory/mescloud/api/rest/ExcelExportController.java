package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.service.ExportExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/export")
@AllArgsConstructor
public class ExcelExportController {

    private final ExportExcelService exportExcelService;

    @GetMapping("production-orders")
    public void exportProductionOrderToExcel(HttpServletResponse response) {
        exportExcelService.exportAllProductionOrderViewToExcel(response);
    }

    @GetMapping("composed-production-orders/without-hits")
    public void exportComposedWithoutHitsToExcel(HttpServletResponse response) {
        exportExcelService.exportAllComposedToExcel(response, false);
    }

    @GetMapping("composed-production-orders/with-hits")
    public void exportComposedWithHitsToExcel(HttpServletResponse response) {
        exportExcelService.exportAllComposedToExcel(response, true);
    }

    @GetMapping("composed-production-orders/completed")
    public void exportCompletedComposedToExcel(HttpServletResponse response) {
        exportExcelService.exportAllCompletedComposedToExcel(response, true);
    }
}