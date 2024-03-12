package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.exception.ExcelExportException;
import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import com.alcegory.mescloud.repository.ComposedProductionOrderRepository;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.service.ExportExcelService;
import com.alcegory.mescloud.utility.export.ExcelExportImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

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
    private static final String COMPOSED_PRODUCTION_ORDERS_COMPLETED = "Produções_Compostas_Completas_Info.xlsx";
    private static final String ERROR_MESSAGE = "Error exporting data to Excel";

    private final ProductionOrderRepository productionOrderRepository;
    private final ComposedProductionOrderRepository composedRepository;

    @Override
    public void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, Map<String, String> requestPayload) {
        Timestamp startDate = stringToTimestamp(requestPayload.get(START_DATE));
        Timestamp endDate = stringToTimestamp(requestPayload.get(END_DATE));

        setExcelResponseHeaders(response, COMPOSED_PRODUCTION_ORDERS_COMPLETED);

        List<ProductionOrderSummaryEntity> productionOrderViews = productionOrderRepository.findCompleted(startDate, endDate, false, null);
        List<ComposedSummaryEntity> composedList = composedRepository.findAllComposed(startDate, endDate);

        try {
            if (productionOrderViews.isEmpty() && composedList.isEmpty()) {
                handleEmptyLists(response);
            } else {
                ExcelExportImpl excelExportImpl = new ExcelExportImpl();
                excelExportImpl.exportDataToExcel(response, composedList, productionOrderViews);
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
