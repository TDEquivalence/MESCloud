package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.exception.ExcelExportException;
import com.alcegory.mescloud.model.dto.KpiFilterDto;
import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.repository.ComposedProductionOrderRepository;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.service.ExportExcelService;
import com.alcegory.mescloud.utility.export.ExcelExportComposed;
import com.alcegory.mescloud.utility.export.ExcelExportProductionOrder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import static com.alcegory.mescloud.model.filter.Filter.Property.END_DATE;
import static com.alcegory.mescloud.model.filter.Filter.Property.START_DATE;

@Service
@AllArgsConstructor
@Log
public class ExportExcelServiceImpl implements ExportExcelService {

    private static final String SHEET_NAME_PRODUCTION_ORDERS = "Ordens de Produção";
    private static final String SHEET_NAME_COMPOSED = "Produções Compostas";
    private static final String SHEET_NAME_COMPLETED = "Produções Concluídas";
    private static final String PRODUCTION_ORDERS = "Ordens_de_Produção_Info.xlsx";
    private static final String COMPOSED_PRODUCTION_ORDERS = "Produções_Compostas_Info.xlsx";
    private static final String COMPOSED_PRODUCTION_ORDERS_WITH_HITS = "Produções_Compostas_Hits_Info.xlsx";
    private static final String COMPOSED_PRODUCTION_ORDERS_COMPLETED = "Produções_Compostas_Completas_Info.xlsx";

    private static final String ERROR_MESSAGE = "Error exporting data to Excel";

    private final ProductionOrderRepository productionOrderRepository;
    private final ComposedProductionOrderRepository composedRepository;

    @Override
    public void exportProductionOrderViewToExcel(HttpServletResponse response, KpiFilterDto filter) {
        Timestamp startDate = getDate(filter, START_DATE);
        Timestamp endDate = getDate(filter, END_DATE);
        setExcelResponseHeaders(response, PRODUCTION_ORDERS);
        List<ProductionOrderSummaryEntity> productionOrderViews = productionOrderRepository.findCompletedWithoutComposed(startDate, endDate);
        ExcelExportProductionOrder abstractExcelExport = new ExcelExportProductionOrder(productionOrderViews, SHEET_NAME_PRODUCTION_ORDERS);
        try {
            abstractExcelExport.exportDataToExcel(response);
        } catch (IOException e) {
            throw new ExcelExportException(ERROR_MESSAGE, e);
        }
    }

    @Override
    public void exportComposedToExcel(HttpServletResponse response, boolean withHits, KpiFilterDto filter) {
        Timestamp startDate = getDate(filter, START_DATE);
        Timestamp endDate = getDate(filter, END_DATE);

        setExcelResponseHeaders(response, withHits ? COMPOSED_PRODUCTION_ORDERS_WITH_HITS : COMPOSED_PRODUCTION_ORDERS);
        List<ComposedSummaryEntity> composedList = composedRepository.getOpenComposedSummaries(withHits, startDate, endDate);
        ExcelExportComposed excelExportComposed = new ExcelExportComposed(composedList, withHits, SHEET_NAME_COMPOSED, false);
        try {
            excelExportComposed.exportDataToExcel(response);
        } catch (IOException e) {
            throw new ExcelExportException(ERROR_MESSAGE, e);
        }
    }

    @Override
    public void exportCompletedComposedToExcel(HttpServletResponse response, boolean withHits, KpiFilterDto filter) {
        Timestamp startDate = getDate(filter, START_DATE);
        Timestamp endDate = getDate(filter, END_DATE);

        setExcelResponseHeaders(response, COMPOSED_PRODUCTION_ORDERS_COMPLETED);
        List<ComposedSummaryEntity> composedList = composedRepository.findCompleted(startDate, endDate);
        ExcelExportComposed excelExportComposed = new ExcelExportComposed(composedList, withHits, SHEET_NAME_COMPLETED, true);
        try {
            excelExportComposed.exportDataToExcel(response);
        } catch (IOException e) {
            throw new ExcelExportException(ERROR_MESSAGE, e);
        }
    }

    private void setExcelResponseHeaders(HttpServletResponse response, String filename) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
    }

    private Timestamp getDate(KpiFilterDto filter, Filter.Property property) {
        if (filter == null) {
            return null;
        }
        return filter.getSearch().getTimestampValue(property);
    }
}
