package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.exception.ExcelExportException;
import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import com.alcegory.mescloud.repository.ComposedProductionOrderRepository;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.service.ExportExcelService;
import com.alcegory.mescloud.utility.export.ExcelExportComposed;
import com.alcegory.mescloud.utility.export.ExcelExportProductionOrder;
import com.alcegory.mescloud.utility.export.MultiExcelExport;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Log
public class ExportExcelServiceImpl implements ExportExcelService {

    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String SHEET_NAME_PRODUCTION_ORDERS = "Ordens de Produção";
    private static final String SHEET_NAME_COMPLETED = "Produções Concluídas";
    private static final String PRODUCTION_ORDERS = "Ordens_de_Produção_Info.xlsx";
    private static final String COMPOSED_PRODUCTION_ORDERS_COMPLETED = "Produções_Compostas_Completas_Info.xlsx";
    private static final String ERROR_MESSAGE = "Error exporting data to Excel";

    private final ProductionOrderRepository productionOrderRepository;
    private final ComposedProductionOrderRepository composedRepository;

    @Override
    public void exportProductionOrderViewToExcelFiltered(HttpServletResponse response, @RequestBody Map<String, String> requestPayload) {
        Timestamp startDate = stringToTimestamp(requestPayload.get(START_DATE));
        Timestamp endDate = stringToTimestamp(requestPayload.get(END_DATE));

        setExcelResponseHeaders(response, PRODUCTION_ORDERS);
        List<ProductionOrderSummaryEntity> productionOrderViews = productionOrderRepository.findCompleted(startDate, endDate,
                true);
        ExcelExportProductionOrder abstractExcelExport = new ExcelExportProductionOrder(productionOrderViews,
                SHEET_NAME_PRODUCTION_ORDERS, false);
        abstractExcelExport.exportDataToExcel(response);
    }

    @Override
    public void exportCompletedComposedToExcelFiltered(HttpServletResponse response, boolean withHits, Map<String, String> requestPayload) {
        Timestamp startDate = stringToTimestamp(requestPayload.get(START_DATE));
        Timestamp endDate = stringToTimestamp(requestPayload.get(END_DATE));

        setExcelResponseHeaders(response, COMPOSED_PRODUCTION_ORDERS_COMPLETED);
        List<ComposedSummaryEntity> composedList = composedRepository.findCompleted(startDate, endDate);
        ExcelExportComposed excelExportComposed = new ExcelExportComposed(composedList, withHits, SHEET_NAME_COMPLETED, true);
        excelExportComposed.exportDataToExcel(response);
    }

    @Override
    public void exportAllProductionAndComposedToExcel(HttpServletResponse response) {
        setExcelResponseHeaders(response, COMPOSED_PRODUCTION_ORDERS_COMPLETED);
        List<ProductionOrderSummaryEntity> productionOrderViews = productionOrderRepository.findCompleted(null,
                null, false);
        List<ComposedSummaryEntity> composedList = composedRepository.findCompleted(null, null);

        MultiExcelExport multiExcelExport = new MultiExcelExport();
        try {
            multiExcelExport.exportDataToExcel(response, composedList, productionOrderViews);
        } catch (IOException e) {
            throw new ExcelExportException(ERROR_MESSAGE, e);
        }
    }

    @Override
    public void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, Map<String, String> requestPayload) {
        Timestamp startDate = stringToTimestamp(requestPayload.get(START_DATE));
        Timestamp endDate = stringToTimestamp(requestPayload.get(END_DATE));

        setExcelResponseHeaders(response, COMPOSED_PRODUCTION_ORDERS_COMPLETED);

        List<ProductionOrderSummaryEntity> productionOrderViews = productionOrderRepository.findCompleted(startDate, endDate, false);
        List<ComposedSummaryEntity> composedList = composedRepository.findCompleted(startDate, endDate);

        try {
            if (productionOrderViews.isEmpty() && composedList.isEmpty()) {
                handleEmptyLists(response);
            } else if (productionOrderViews.isEmpty()) {
                exportCompletedComposedToExcelFiltered(response, true, requestPayload);
            } else if (composedList.isEmpty()) {
                exportProductionOrderViewToExcelFiltered(response, requestPayload);
            } else {
                MultiExcelExport multiExcelExport = new MultiExcelExport();
                multiExcelExport.exportDataToExcel(response, composedList, productionOrderViews);
            }
        } catch (IOException e) {
            throw new ExcelExportException(ERROR_MESSAGE, e);
        }
    }

    private void handleEmptyLists(HttpServletResponse response) throws IOException {
        setExcelResponseHeaders(response, COMPOSED_PRODUCTION_ORDERS_COMPLETED);
        try (Workbook workbook = new XSSFWorkbook();
             OutputStream outputStream = response.getOutputStream()) {
            workbook.createSheet("No Data Available");
            workbook.write(outputStream);
        }
    }

    public static Timestamp stringToTimestamp(String dateString) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
        return Timestamp.valueOf(localDateTime);
    }


    private void setExcelResponseHeaders(HttpServletResponse response, String filename) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
    }
}
