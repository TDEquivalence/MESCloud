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

    @GetMapping("production-orders")
    public void exportAllProductionOrderToExcel(HttpServletResponse response) {
        exportExcelService.exportProductionOrderViewToExcel(response, null);
    }

    @PostMapping("production-orders")
    public void exportProductionOrderToExcel(HttpServletResponse response, KpiFilterDto filter) {
        exportExcelService.exportProductionOrderViewToExcel(response, filter);
    }

    @GetMapping("composed-production-orders/without-hits")
    public void exportAllComposedWithoutHitsToExcel(HttpServletResponse response) {
        exportExcelService.exportComposedToExcel(response, false, null);
    }

    @PostMapping("composed-production-orders/without-hits")
    public void exportComposedWithoutHitsToExcel(HttpServletResponse response, KpiFilterDto filter) {
        exportExcelService.exportComposedToExcel(response, false, null);
    }

    @GetMapping("composed-production-orders/with-hits")
    public void exportAllComposedWithHitsToExcel(HttpServletResponse response) {
        exportExcelService.exportComposedToExcel(response, true, null);
    }

    @PostMapping("composed-production-orders/with-hits")
    public void exportComposedWithHitsToExcel(HttpServletResponse response, KpiFilterDto filter) {
        exportExcelService.exportComposedToExcel(response, true, null);
    }

    @GetMapping("composed-production-orders/completed")
    public void exportAllCompletedComposedToExcel(HttpServletResponse response) {
        exportExcelService.exportCompletedComposedToExcel(response, true, null);
    }

    @PostMapping("composed-production-orders/completed")
    public void exportCompletedComposedToExcel(HttpServletResponse response, KpiFilterDto filter) {
        exportExcelService.exportCompletedComposedToExcel(response, true, filter);
    }
}