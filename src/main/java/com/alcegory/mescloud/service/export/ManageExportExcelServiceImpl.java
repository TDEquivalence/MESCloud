package com.alcegory.mescloud.service.export;

import com.alcegory.mescloud.exception.ExcelExportException;
import com.alcegory.mescloud.model.converter.ComposedConverter;
import com.alcegory.mescloud.model.converter.ProductionOrderConverter;
import com.alcegory.mescloud.model.dto.composed.ComposedExportInfoDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderExportInfoDto;
import com.alcegory.mescloud.model.entity.composed.ComposedSummaryEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.repository.composed.ComposedProductionOrderRepository;
import com.alcegory.mescloud.repository.production.ProductionOrderRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class ManageExportExcelServiceImpl implements ManageExportExcelService {

    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String COMPOSED_PRODUCTION_ORDERS_COMPLETED = "Produções_Compostas_Completas_Info.xlsx";
    private static final String ERROR_MESSAGE = "Error exporting data to Excel";

    private final ProductionOrderRepository productionOrderRepository;
    private final ComposedProductionOrderRepository composedRepository;
    private final ComposedConverter composedConverter;
    private final ProductionOrderConverter productionOrderConverter;

    public static Timestamp stringToTimestamp(String dateString) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
        return Timestamp.valueOf(localDateTime);
    }

    @Override
    public void exportProductionAndComposedToExcelFiltered(HttpServletResponse response, Map<String, String> requestPayload, long sectionId) {
        Timestamp startDate = stringToTimestamp(requestPayload.get(START_DATE));
        Timestamp endDate = stringToTimestamp(requestPayload.get(END_DATE));

        setExcelResponseHeaders(response, COMPOSED_PRODUCTION_ORDERS_COMPLETED);

        List<ProductionOrderEntity> productionOrders = productionOrderRepository.findCompleted(sectionId, false,
                null, startDate, endDate);
        List<ComposedSummaryEntity> composedList = composedRepository.findAllComposed(startDate, endDate);

        List<ProductionOrderExportInfoDto> productionOrderDtos = productionOrderConverter.toExportDtoList(productionOrders);
        List<ComposedExportInfoDto> composedExportInfoDtos = composedConverter.convertToDtoList(composedList);

        try {
            if (productionOrderDtos.isEmpty() && composedExportInfoDtos.isEmpty()) {
                handleEmptyLists(response);
            } else {
                ExportExcelImpl excelExportImpl = new ExportExcelImpl();
                excelExportImpl.exportDataToExcel(response, composedExportInfoDtos, productionOrderDtos);
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

    private void setExcelResponseHeaders(HttpServletResponse response, String filename) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
    }
}
