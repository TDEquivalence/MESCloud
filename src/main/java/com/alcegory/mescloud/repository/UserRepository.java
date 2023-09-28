package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.dto.UserFilter;
import com.alcegory.mescloud.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    UserEntity findUserByUsername(String username);

    UserEntity findUserById(Long id);

    List<UserEntity> getFilteredUsers(UserFilter filter);
}
