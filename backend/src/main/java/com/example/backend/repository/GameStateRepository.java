package com.example.backend.repository;

import com.example.backend.domain.GameState;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for GameState entity.
 *
 * PUBLIC_INTERFACE
 */
@Repository
public interface GameStateRepository extends JpaRepository<GameState, Long> {

    // PUBLIC_INTERFACE
    /**
     * Retrieve game state by room id.
     * @param roomId room id
     * @return optional game state
     */
    Optional<GameState> findByRoom_Id(Long roomId);
}
