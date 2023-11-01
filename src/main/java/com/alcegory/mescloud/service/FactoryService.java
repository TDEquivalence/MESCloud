package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.FactoryDto;

import java.util.List;

public interface FactoryService {

    FactoryDto saveFactory(FactoryDto factoryDto);

    FactoryDto getFactoryById(Long id);

    FactoryDto getFactoryByName(String name);

    List<FactoryDto> getAllFactories();

    void deleteFactoryById(Long id);

    void deleteFactoryByName(String name);
}
