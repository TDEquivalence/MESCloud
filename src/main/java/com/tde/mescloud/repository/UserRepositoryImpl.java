package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.UserWinnow;
import com.tde.mescloud.model.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl extends AbstractWinnowRepository<UserWinnow.Property, UserEntity> {


    public List<UserEntity> findAllWithWinnow(UserWinnow winnow) {
        return super.findAllWithWinnow(winnow, UserEntity.class);
    }
}
