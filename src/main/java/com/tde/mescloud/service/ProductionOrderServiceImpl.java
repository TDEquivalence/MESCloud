package com.tde.mescloud.service;

import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.converter.ProductionOrderConverter;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.repository.ProductionOrderRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class ProductionOrderServiceImpl implements ProductionOrderService{

    private static final String CODE_PREFIX = "PO";
    private static final int CODE_VALUE_INDEX = 4;

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

    @Override
    public String generateCode() {
        return CODE_PREFIX + getYearForCode() + getNewCodeValue();
    }

    private int getYearForCode() {
        return Calendar.getInstance().get(Calendar.YEAR) % 100;
    }

    private int getNewCodeValue() {
        ProductionOrderEntity productionOrderEntity = repository.findTopByOrderByIdDesc();
        String lastValueAsString = productionOrderEntity.getCode().substring(4);
        int lastCodeValue = Integer.parseInt(lastValueAsString);
        return ++lastCodeValue;
    }
}
