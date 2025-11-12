package com.example.backend.controller;

import com.example.backend.dto.GameEventEnvelope;
import com.example.backend.dto.GameStateDTO;
import com.example.backend.dto.actions.MoveRequest;
import com.example.backend.dto.actions.RollRequest;
import com.example.backend.service.BroadcastService;
import com.example.backend.service.GameService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * STOMP WebSocket message controller. Clients send to /app/game/{code}/action.*
 * Server broadcasts updates to /topic/game/{code}.
 *
 * PUBLIC_INTERFACE
 */
@Controller
@Validated
@Tag(name = "Game WS", description = "STOMP endpoints: send to /app/game/{code}/action.*; subscribe /topic/game/{code}")
public class GameWsController {

    private final GameService gameService;
    private final BroadcastService broadcastService;

    public GameWsController(GameService gameService, BroadcastService broadcastService) {
        this.gameService = gameService;
        this.broadcastService = broadcastService;
    }

    // PUBLIC_INTERFACE
    /**
     * Handle dice roll over STOMP.
     *
     * @param code room code
     * @param req roll request
     */
    @MessageMapping("/game/{code}/action.roll")
    public void roll(@DestinationVariable String code, @Valid RollRequest req) {
        GameStateDTO dto = gameService.rollDice(code, req.getColor());
        broadcastService.broadcast(code, GameEventEnvelope.of("DiceRolled", dto, code));
    }

    // PUBLIC_INTERFACE
    /**
     * Handle move over STOMP.
     */
    @MessageMapping("/game/{code}/action.move")
    public void move(@DestinationVariable String code, @Valid MoveRequest req) {
        GameStateDTO dto = gameService.moveToken(code, req.getColor(), req.getTokenIndex(), req.getDice());
        broadcastService.broadcast(code, GameEventEnvelope.of("GameStateUpdated", dto, code));
    }
}
