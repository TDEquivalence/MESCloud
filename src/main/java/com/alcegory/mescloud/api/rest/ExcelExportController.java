package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.api.rest.base.SectionBaseController;
import com.alcegory.mescloud.service.export.ManageExportExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
@AllArgsConstructor
public class ExcelExportController extends SectionBaseController {

    private static final String EXPORT_URL = "/export";

    private final ManageExportExcelService exportExcelService;

    @PostMapping(EXPORT_URL + "composed-and-production-orders/completed")
    public ResponseEntity<Void> exportCompletedComposedAndProductionToExcel(HttpServletResponse response,
                                                                            @RequestBody Map<String, String> requestPayload,
                                                                            @PathVariable long sectionId) {
        exportExcelService.exportProductionAndComposedToExcelFiltered(response, requestPayload, sectionId);
        return ResponseEntity.ok().build();
    }
}