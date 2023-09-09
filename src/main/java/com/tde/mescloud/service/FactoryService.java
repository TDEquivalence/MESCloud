package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.FactoryDto;

import java.util.List;

public interface FactoryService {

    FactoryDto saveFactory(FactoryDto factoryDto);

    FactoryDto getFactoryById(Long id);

    FactoryDto getFactoryByName(String name);

    List<FactoryDto> getAllFactories();

    void deleteFactoryById(Long id);

    void deleteFactoryByName(String name);
}
