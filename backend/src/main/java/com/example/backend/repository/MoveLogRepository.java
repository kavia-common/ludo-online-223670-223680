package com.example.backend.repository;

import com.example.backend.domain.MoveLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for MoveLog entity.
 *
 * PUBLIC_INTERFACE
 */
@Repository
public interface MoveLogRepository extends JpaRepository<MoveLog, Long> {

    // PUBLIC_INTERFACE
    /**
     * Retrieve move logs for a room ordered by creation time.
     * @param roomId room id
     * @return list of move logs
     */
    List<MoveLog> findByRoom_IdOrderByCreatedAtAsc(Long roomId);
}
