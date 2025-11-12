package com.example.backend.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Serializable current game state representation for clients.
 *
 * PUBLIC_INTERFACE
 */
public class GameStateDTO {
    private String roomCode;
    private String currentTurnColor;
    private Integer lastDiceRoll;
    private Map<String, Object> board; // arbitrary map parsed from boardStateJson
    private Instant updatedAt;

    public String getRoomCode() {
        return roomCode;
    }

    public GameStateDTO setRoomCode(String roomCode) {
        this.roomCode = roomCode;
        return this;
    }

    public String getCurrentTurnColor() {
        return currentTurnColor;
    }

    public GameStateDTO setCurrentTurnColor(String currentTurnColor) {
        this.currentTurnColor = currentTurnColor;
        return this;
    }

    public Integer getLastDiceRoll() {
        return lastDiceRoll;
    }

    public GameStateDTO setLastDiceRoll(Integer lastDiceRoll) {
        this.lastDiceRoll = lastDiceRoll;
        return this;
    }

    public Map<String, Object> getBoard() {
        return board;
    }

    public GameStateDTO setBoard(Map<String, Object> board) {
        this.board = board;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public GameStateDTO setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}
