package com.se.backend.repositories;

import com.se.backend.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long>, JpaSpecificationExecutor<Token> {
    Optional<Token> findByToken(String token);
}