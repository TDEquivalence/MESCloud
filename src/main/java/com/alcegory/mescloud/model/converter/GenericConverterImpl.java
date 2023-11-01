package com.alcegory.mescloud.model.converter;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class GenericConverterImpl<T, D> implements GenericConverter<T, D> {

    private final ModelMapper modelMapper;


    public T toEntity(D dto, Class<T> entityClass) {
        return modelMapper.map(dto, entityClass);
    }

    public D toDto(T entity, Class<D> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    public List<D> toDto(List<T> entityList, Class<D> dtoClass) {
        return entityList.stream()
                .map(entity -> toDto(entity, dtoClass))
                .toList();
    }

    public List<T> toEntity(List<D> dtoList, Class<T> entityClass) {
        return dtoList.stream()
                .map(dto -> toEntity(dto, entityClass))
                .toList();
    }
}
