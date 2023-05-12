package com.tde.mescloud.security.repository;

import com.tde.mescloud.security.model.token.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    @Query("SELECT t FROM token t JOIN t.user u WHERE u.id = :userId AND (t.expired = false OR t.revoked = false)")
    List<TokenEntity> findAllValidTokenByUser(Long userId);

    @Query("SELECT t FROM token t JOIN t.user u WHERE u.id = :userId AND (t.expired = true OR t.revoked = true)")
    List<TokenEntity> findAllInvalidTokenByUser(Long userId);

    Optional<TokenEntity> findByToken(String token);
}