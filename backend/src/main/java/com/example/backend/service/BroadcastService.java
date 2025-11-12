package com.example.backend.service;

import com.example.backend.dto.GameEventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Wrapper around SimpMessagingTemplate for broadcasting events.
 *
 * PUBLIC_INTERFACE
 */
@Service
public class BroadcastService {
    private static final Logger log = LoggerFactory.getLogger(BroadcastService.class);
    private final SimpMessagingTemplate template;

    public BroadcastService(SimpMessagingTemplate template) {
        this.template = template;
    }

    // PUBLIC_INTERFACE
    /**
     * Broadcast to /topic/game/{code}.
     *
     * @param roomCode room code
     * @param envelope event payload
     */
    public void broadcast(String roomCode, GameEventEnvelope envelope) {
        String destination = "/topic/game/" + roomCode;
        log.info("broadcasting event type={} to destination={}", envelope.getType(), destination);
        template.convertAndSend(destination, envelope);
    }
}
