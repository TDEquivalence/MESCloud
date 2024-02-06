package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.service.ProductionOrderViewService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/export")
@AllArgsConstructor
public class ExcelExportController {

    private final ProductionOrderViewService productionOrderViewService;

    @GetMapping("production-orders")
    public void exportToExcel(HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Ordens_de_Produção_Info-xlsx";

        response.setHeader(headerKey, headerValue);
        productionOrderViewService.exportProductionOrderViewToExcel(response);
    }
}