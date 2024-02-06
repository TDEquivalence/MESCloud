package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.entity.ProductionOrderView;
import com.alcegory.mescloud.repository.ProductionOrderViewRepository;
import com.alcegory.mescloud.service.ProductionOrderViewService;
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
public class ProductionOrderViewServiceImpl implements ProductionOrderViewService {

    private ProductionOrderViewRepository repository;

    public List<ProductionOrderView> exportProductionOrderViewToExcel(HttpServletResponse response) {
        List<ProductionOrderView> productionOrderViews = repository.findAll();
        ExcelExportUtil excelExportUtil = new ExcelExportUtil(productionOrderViews);
        try {
            excelExportUtil.exportDataToExcel(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return productionOrderViews;
    }
}
