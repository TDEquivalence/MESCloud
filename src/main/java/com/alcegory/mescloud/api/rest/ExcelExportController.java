package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.service.export.ExportExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/export")
@AllArgsConstructor
public class ExcelExportController {

    private final ExportExcelService exportExcelService;

    @PostMapping("composed-and-production-orders/completed")
    public ResponseEntity<Void> exportCompletedComposedAndProductionToExcel(HttpServletResponse response,
                                                                            @RequestBody Map<String, String> requestPayload) {
        try {
            exportExcelService.exportProductionAndComposedToExcelFiltered(response, requestPayload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}