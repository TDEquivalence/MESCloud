package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.dto.UserFilter;
import com.alcegory.mescloud.model.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl extends AbstractFilterRepository<UserFilter.Property, UserEntity> {


    public List<UserEntity> getFilteredUsers(UserFilter filter) {
        return super.findAllWithFilter(filter, UserEntity.class);
    }
}
