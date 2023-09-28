package com.tde.mescloud.model.converter;

import java.util.List;

public interface GenericConverter<T, D> {

    T toEntity(D dto, Class<T> entityClass);

    D toDto(T entity, Class<D> dtoClass);

    List<D> toDto(List<T> entityList, Class<D> dtoClass);

    List<T> toEntity(List<D> dtoList, Class<T> entityClass);
}
