package com.example.backend.repository;

import com.example.backend.domain.Room;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Room entity.
 *
 * PUBLIC_INTERFACE
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // PUBLIC_INTERFACE
    /**
     * Finds a room by its roomCode.
     * @param roomCode unique code of the room
     * @return optional room
     */
    Optional<Room> findByRoomCode(String roomCode);

    // PUBLIC_INTERFACE
    /**
     * Checks existence by roomCode.
     * @param roomCode code to check
     * @return true if exists
     */
    boolean existsByRoomCode(String roomCode);
}
