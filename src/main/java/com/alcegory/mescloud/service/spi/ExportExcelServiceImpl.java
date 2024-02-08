package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.exception.ExcelExportException;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.service.ExportExcelService;
import com.alcegory.mescloud.utility.export.AbstractExcelExport;
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

    private static final String PRODUCTION_ORDERS = "Ordens_de_Produção_Info.xlsx";

    private ProductionOrderRepository repository;

    public List<ProductionOrderSummaryEntity> exportProductionOrderViewToExcel(HttpServletResponse response) {
        setExcelResponseHeaders(response, PRODUCTION_ORDERS);
        List<ProductionOrderSummaryEntity> productionOrderViews = repository.findCompletedWithoutComposed();
        AbstractExcelExport abstractExcelExport = new ExcelExportProductionOrder(productionOrderViews);
        try {
            abstractExcelExport.exportDataToExcel(response);
        } catch (IOException e) {
            throw new ExcelExportException("Error exporting data to Excel", e);
        }
        return productionOrderViews;
    }

    @Override
    public void setExcelResponseHeaders(HttpServletResponse response, String filename) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
    }
}
