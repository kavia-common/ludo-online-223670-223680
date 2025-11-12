package com.example.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game room identified by a unique roomCode.
 *
 * PUBLIC_INTERFACE
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique public code that players use to join the room.
     */
    @Column(nullable = false, unique = true, length = 16)
    private String roomCode;

    /**
     * Optional display name for the room.
     */
    @Column(length = 100)
    private String name;

    /**
     * Indicates if the room is active/open for gameplay.
     */
    @Column(nullable = false)
    private boolean active = true;

    /**
     * Timestamp of creation.
     */
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /**
     * Players in this room. Simple unidirectional mapping for convenience.
     * Cascade persists so creating players when saving a room is easy during development.
     */
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    /**
     * Current game state associated with the room.
     */
    @OneToOne(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private GameState gameState;

    public Room() {
    }

    public Room(String roomCode, String name) {
        this.roomCode = roomCode;
        this.name = name;
    }

    // PUBLIC_INTERFACE
    /**
     * Add a player to the room and set relationship.
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        players.add(player);
        player.setRoom(this);
    }

    // PUBLIC_INTERFACE
    /**
     * Remove a player from the room and clear relationship.
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        players.remove(player);
        player.setRoom(null);
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getName() {
        return name;
    }

    public Room setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public Room setActive(boolean active) {
        this.active = active;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        if (gameState != null && gameState.getRoom() != this) {
            gameState.setRoom(this);
        }
    }
}
