package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.service.ExportExcelService;
import com.alcegory.mescloud.utility.ExcelExportUtil;
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

    private ProductionOrderRepository repository;

    public List<ProductionOrderSummaryEntity> exportProductionOrderViewToExcel(HttpServletResponse response) {
        List<ProductionOrderSummaryEntity> productionOrderViews = repository.findCompletedWithoutComposed();
        ExcelExportUtil excelExportUtil = new ExcelExportUtil(productionOrderViews);
        try {
            excelExportUtil.exportDataToExcel(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return productionOrderViews;
    }
}
