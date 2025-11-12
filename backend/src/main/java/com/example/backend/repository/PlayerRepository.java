package com.example.backend.repository;

import com.example.backend.domain.Player;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Player entity.
 *
 * PUBLIC_INTERFACE
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // PUBLIC_INTERFACE
    /**
     * Find all players in a room ordered by turnOrder.
     * @param roomId room id
     * @return players
     */
    List<Player> findByRoom_IdOrderByTurnOrderAsc(Long roomId);

    // PUBLIC_INTERFACE
    /**
     * Find a player by room and color.
     * @param roomId room id
     * @param color color string
     * @return optional player
     */
    Optional<Player> findByRoom_IdAndColor(Long roomId, String color);
}
