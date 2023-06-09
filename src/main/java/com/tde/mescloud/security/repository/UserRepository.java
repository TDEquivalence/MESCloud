package com.tde.mescloud.security.repository;

import com.tde.mescloud.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);
    UserEntity findUserByUsername(String username);
    UserEntity findUserById(Long id);
}
