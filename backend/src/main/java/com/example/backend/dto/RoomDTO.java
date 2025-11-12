package com.example.backend.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Room information for clients.
 *
 * PUBLIC_INTERFACE
 */
public class RoomDTO {
    private String roomCode;
    private String name;
    private boolean active;
    private Instant createdAt;
    private List<PlayerDTO> players = new ArrayList<>();

    public String getRoomCode() {
        return roomCode;
    }

    public RoomDTO setRoomCode(String roomCode) {
        this.roomCode = roomCode;
        return this;
    }

    public String getName() {
        return name;
    }

    public RoomDTO setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public RoomDTO setActive(boolean active) {
        this.active = active;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public RoomDTO setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public RoomDTO setPlayers(List<PlayerDTO> players) {
        this.players = players;
        return this;
    }
}
