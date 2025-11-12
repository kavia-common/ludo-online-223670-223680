package com.example.backend.domain;

import jakarta.persistence.*;

/**
 * Represents a player within a Room.
 *
 * PUBLIC_INTERFACE
 */
@Entity
@Table(name = "players", indexes = {
        @Index(name = "idx_player_room", columnList = "room_id")
})
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Display name of the player.
     */
    @Column(nullable = false, length = 64)
    private String name;

    /**
     * Player color in the Ludo game: RED, BLUE, GREEN, YELLOW.
     */
    @Column(nullable = false, length = 16)
    private String color;

    /**
     * Whether this player is AI-controlled.
     */
    @Column(nullable = false)
    private boolean ai = false;

    /**
     * Order of turn (0..3) for the room.
     */
    @Column(nullable = false)
    private int turnOrder;

    /**
     * Owning room.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    public Player() {
    }

    public Player(String name, String color, boolean ai, int turnOrder) {
        this.name = name;
        this.color = color;
        this.ai = ai;
        this.turnOrder = turnOrder;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Player setName(String name) {
        this.name = name;
        return this;
    }

    public String getColor() {
        return color;
    }

    public Player setColor(String color) {
        this.color = color;
        return this;
    }

    public boolean isAi() {
        return ai;
    }

    public Player setAi(boolean ai) {
        this.ai = ai;
        return this;
    }

    public int getTurnOrder() {
        return turnOrder;
    }

    public Player setTurnOrder(int turnOrder) {
        this.turnOrder = turnOrder;
        return this;
    }

    public Room getRoom() {
        return room;
    }

    public Player setRoom(Room room) {
        this.room = room;
        return this;
    }
}
