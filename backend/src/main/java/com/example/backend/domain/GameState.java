package com.example.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Represents the current game state for a Room.
 * Stores board state as a JSON String for simplicity.
 *
 * PUBLIC_INTERFACE
 */
@Entity
@Table(name = "game_states")
public class GameState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owning room (1:1).
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false, unique = true)
    private Room room;

    /**
     * JSON representation of the board state.
     */
    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String boardStateJson;

    /**
     * Current player's color to move.
     */
    @Column(length = 16)
    private String currentTurnColor;

    /**
     * Last dice roll, if any.
     */
    @Column
    private Integer lastDiceRoll;

    /**
     * Updated timestamp for optimistic visibility (not strict optimistic locking).
     */
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public GameState() {
    }

    public GameState(Room room, String boardStateJson) {
        this.room = room;
        this.boardStateJson = boardStateJson;
    }

    @PreUpdate
    @PrePersist
    void touch() {
        this.updatedAt = Instant.now();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public GameState setRoom(Room room) {
        this.room = room;
        return this;
    }

    public String getBoardStateJson() {
        return boardStateJson;
    }

    public GameState setBoardStateJson(String boardStateJson) {
        this.boardStateJson = boardStateJson;
        return this;
    }

    public String getCurrentTurnColor() {
        return currentTurnColor;
    }

    public GameState setCurrentTurnColor(String currentTurnColor) {
        this.currentTurnColor = currentTurnColor;
        return this;
    }

    public Integer getLastDiceRoll() {
        return lastDiceRoll;
    }

    public GameState setLastDiceRoll(Integer lastDiceRoll) {
        this.lastDiceRoll = lastDiceRoll;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
