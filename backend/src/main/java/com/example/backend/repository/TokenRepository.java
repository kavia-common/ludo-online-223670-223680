package com.example.backend.repository;

import com.example.backend.domain.Token;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Token entity.
 *
 * PUBLIC_INTERFACE
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    // PUBLIC_INTERFACE
    /**
     * Find tokens for a player ordered by token index.
     * @param playerId player id
     * @return tokens
     */
    List<Token> findByPlayer_IdOrderByTokenIndexAsc(Long playerId);

    // PUBLIC_INTERFACE
    /**
     * Find a single token by player and index.
     * @param playerId player id
     * @param tokenIndex index 0..3
     * @return token or empty
     */
    Optional<Token> findByPlayer_IdAndTokenIndex(Long playerId, int tokenIndex);
}
