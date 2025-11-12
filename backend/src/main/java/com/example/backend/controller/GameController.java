package com.example.backend.controller;

import com.example.backend.dto.GameStateDTO;
import com.example.backend.dto.actions.MoveRequest;
import com.example.backend.dto.actions.RollRequest;
import com.example.backend.service.GameService;
import com.example.backend.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Game REST endpoints for state and actions.
 *
 * PUBLIC_INTERFACE
 */
@RestController
@RequestMapping("/api/rooms/{code}")
@Tag(name = "Game", description = "Game state and actions")
@Validated
public class GameController {

    private final GameService gameService;
    private final RoomService roomService;

    public GameController(GameService gameService, RoomService roomService) {
        this.gameService = gameService;
        this.roomService = roomService;
    }

    // PUBLIC_INTERFACE
    /**
     * Get current game state of a room.
     */
    @GetMapping("/state")
    @Operation(summary = "Get game state", description = "Returns the current game state for the room")
    public GameStateDTO state(@PathVariable("code") String code) {
        return gameService.getState(code);
    }

    // PUBLIC_INTERFACE
    /**
     * Roll dice for the current player.
     */
    @PostMapping("/action/roll")
    @Operation(summary = "Roll dice", description = "Roll dice for the player whose turn it is")
    public GameStateDTO roll(@PathVariable("code") String code, @Valid @RequestBody RollRequest req) {
        return gameService.rollDice(code, req.getColor());
    }

    // PUBLIC_INTERFACE
    /**
     * Move a token for the current player using last rolled dice or provided diceOverride.
     */
    @PostMapping("/action/move")
    @Operation(summary = "Move token", description = "Move a token using the last rolled dice or a provided dice override")
    public GameStateDTO move(@PathVariable("code") String code, @Valid @RequestBody MoveRequest req) {
        return gameService.moveToken(code, req.getColor(), req.getTokenIndex(), req.getDice());
    }
}
