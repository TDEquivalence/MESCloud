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

    private static final String PRODUCTION_ORDERS = "Ordens_de_Produção_Info.xlsx";

    private final ExportExcelService exportExcelService;

    @GetMapping("production-orders")
    public void exportToExcel(HttpServletResponse response) {
        exportExcelService.setExcelResponseHeaders(response, PRODUCTION_ORDERS);
        exportExcelService.exportProductionOrderViewToExcel(response);
    }
}