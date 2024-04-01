package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.entity.UserEntity;

public interface UserConverter {

    UserConfigDto convertToDtoWithRelatedEntities(UserEntity userEntity);
}
