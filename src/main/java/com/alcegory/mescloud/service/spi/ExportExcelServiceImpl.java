package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.exception.ExcelExportException;
import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
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
import java.util.List;

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
    public void exportAllProductionOrderViewToExcel(HttpServletResponse response) {
        setCsvResponseHeaders(response, PRODUCTION_ORDERS);
        List<ProductionOrderSummaryEntity> productionOrderViews = productionOrderRepository.findCompletedWithoutComposed();
        ExcelExportProductionOrder abstractExcelExport = new ExcelExportProductionOrder(productionOrderViews, SHEET_NAME_PRODUCTION_ORDERS);
        try {
            abstractExcelExport.exportDataToExcel(response);
        } catch (IOException e) {
            throw new ExcelExportException(ERROR_MESSAGE, e);
        }
    }

    @Override
    public void exportAllComposedToExcel(HttpServletResponse response, boolean withHits) {
        setCsvResponseHeaders(response, withHits ? COMPOSED_PRODUCTION_ORDERS_WITH_HITS : COMPOSED_PRODUCTION_ORDERS);
        List<ComposedSummaryEntity> composedList = composedRepository.getOpenComposedSummaries(withHits, null, null);
        ExcelExportComposed excelExportComposed = new ExcelExportComposed(composedList, withHits, SHEET_NAME_COMPOSED, false);
        try {
            excelExportComposed.exportDataToExcel(response);
        } catch (IOException e) {
            throw new ExcelExportException(ERROR_MESSAGE, e);
        }
    }

    @Override
    public void exportAllCompletedComposedToExcel(HttpServletResponse response, boolean withHits) {
        setCsvResponseHeaders(response, COMPOSED_PRODUCTION_ORDERS_COMPLETED);
        List<ComposedSummaryEntity> composedList = composedRepository.findCompleted();
        ExcelExportComposed excelExportComposed = new ExcelExportComposed(composedList, withHits, SHEET_NAME_COMPLETED, true);
        try {
            excelExportComposed.exportDataToExcel(response);
        } catch (IOException e) {
            throw new ExcelExportException(ERROR_MESSAGE, e);
        }
    }

    private void setCsvResponseHeaders(HttpServletResponse response, String filename) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".csv");
    }
}
