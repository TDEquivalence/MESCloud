package com.tde.mescloud.service;

import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.converter.ProductionOrderConverter;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.repository.ProductionOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductionOrderServiceImpl implements ProductionOrderService{

    private final ProductionOrderRepository repository;
    private final ProductionOrderConverter converter;

    public ProductionOrderServiceImpl(ProductionOrderRepository repository, ProductionOrderConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public ProductionOrder findByCode(String code) {
        ProductionOrderEntity entity = repository.findByCode(code);
        return converter.convertToDO(entity);
    }
}
