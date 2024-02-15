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

    @GetMapping("production-orders/all")
    public void exportAllProductionOrderToExcel(HttpServletResponse response) {
        exportExcelService.exportAllProductionOrderViewToExcel(response);
    }

    @PostMapping("production-orders")
    public void exportProductionOrderToExcel(HttpServletResponse response, KpiFilterDto filter) {
        exportExcelService.exportProductionOrderViewToExcelFiltered(response, filter);
    }

    @GetMapping("composed-production-orders/without-hits/all")
    public void exportAllComposedWithoutHitsToExcel(HttpServletResponse response) {
        exportExcelService.exportAllComposedToExcel(response, false);
    }

    @PostMapping("composed-production-orders/without-hits")
    public void exportComposedWithoutHitsToExcel(HttpServletResponse response, KpiFilterDto filter) {
        exportExcelService.exportComposedToExcelFiltered(response, false, filter);
    }

    @GetMapping("composed-production-orders/with-hits/all")
    public void exportAllComposedWithHitsToExcel(HttpServletResponse response) {
        exportExcelService.exportAllComposedToExcel(response, true);
    }

    @PostMapping("composed-production-orders/with-hits")
    public void exportComposedWithHitsToExcel(HttpServletResponse response, KpiFilterDto filter) {
        exportExcelService.exportComposedToExcelFiltered(response, true, filter);
    }

    @GetMapping("composed-production-orders/completed/all")
    public void exportAllCompletedComposedToExcel(HttpServletResponse response) {
        exportExcelService.exportAllCompletedComposedToExcel(response, true);
    }

    @PostMapping("composed-production-orders/completed")
    public void exportCompletedComposedToExcel(HttpServletResponse response, KpiFilterDto filter) {
        exportExcelService.exportCompletedComposedToExcelFiltered(response, true, filter);
    }

    @GetMapping("composed-and-production-orders/completed/all")
    public void exportAllCompletedComposedAndProductionToExcel(HttpServletResponse response) {
        exportExcelService.exportAllProductionAndComposedToExcel(response);
    }

    @PostMapping("composed-and-production-orders/completed")
    public void exportCompletedComposedAndProductionToExcel(HttpServletResponse response, KpiFilterDto filter) {
        exportExcelService.exportProductionAndComposedToExcelFiltered(response, filter);
    }
}