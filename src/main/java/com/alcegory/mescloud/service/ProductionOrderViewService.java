package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.entity.ProductionOrderView;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface ProductionOrderViewService {

    List<ProductionOrderView> exportProductionOrderViewToExcel(HttpServletResponse response);
}
