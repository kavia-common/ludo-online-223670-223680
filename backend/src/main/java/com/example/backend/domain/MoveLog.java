package com.example.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Log entry for a move performed in a Room.
 *
 * PUBLIC_INTERFACE
 */
@Entity
@Table(name = "move_logs", indexes = {
        @Index(name = "idx_movelog_room", columnList = "room_id"),
        @Index(name = "idx_movelog_created", columnList = "createdAt")
})
public class MoveLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated room for the move.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /**
     * Player color that made the move (helps avoid eager joins).
     */
    @Column(length = 16, nullable = false)
    private String playerColor;

    /**
     * Token index that moved (0..3).
     */
    @Column(nullable = false)
    private int tokenIndex;

    /**
     * From position (see Token.position format).
     */
    @Column(length = 32)
    private String fromPosition;

    /**
     * To position.
     */
    @Column(length = 32)
    private String toPosition;

    /**
     * Dice rolled for the move, if applicable.
     */
    @Column
    private Integer diceValue;

    /**
     * Creation timestamp.
     */
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public MoveLog() {
    }

    public MoveLog(Room room, String playerColor, int tokenIndex, String fromPosition, String toPosition, Integer diceValue) {
        this.room = room;
        this.playerColor = playerColor;
        this.tokenIndex = tokenIndex;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.diceValue = diceValue;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public MoveLog setRoom(Room room) {
        this.room = room;
        return this;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public MoveLog setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
        return this;
    }

    public int getTokenIndex() {
        return tokenIndex;
    }

    public MoveLog setTokenIndex(int tokenIndex) {
        this.tokenIndex = tokenIndex;
        return this;
    }

    public String getFromPosition() {
        return fromPosition;
    }

    public MoveLog setFromPosition(String fromPosition) {
        this.fromPosition = fromPosition;
        return this;
    }

    public String getToPosition() {
        return toPosition;
    }

    public MoveLog setToPosition(String toPosition) {
        this.toPosition = toPosition;
        return this;
    }

    public Integer getDiceValue() {
        return diceValue;
    }

    public MoveLog setDiceValue(Integer diceValue) {
        this.diceValue = diceValue;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
