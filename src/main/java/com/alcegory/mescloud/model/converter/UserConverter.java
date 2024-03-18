package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.entity.UserEntity;

public interface UserConverter {

    UserDto convertToDtoWithRelatedEntities(UserEntity userEntity);
}
