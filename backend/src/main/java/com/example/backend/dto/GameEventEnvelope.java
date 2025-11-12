package com.example.backend.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Envelope for broadcasting events to STOMP subscribers.
 *
 * PUBLIC_INTERFACE
 */
public class GameEventEnvelope {
    private String type;
    private GameStateDTO state;
    private Map<String, Object> meta = new HashMap<>();

    public String getType() {
        return type;
    }

    public GameEventEnvelope setType(String type) {
        this.type = type;
        return this;
    }

    public GameStateDTO getState() {
        return state;
    }

    public GameEventEnvelope setState(GameStateDTO state) {
        this.state = state;
        return this;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public GameEventEnvelope setMeta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    public static GameEventEnvelope of(String type, GameStateDTO state, String roomCode) {
        GameEventEnvelope env = new GameEventEnvelope();
        env.setType(type);
        env.setState(state);
        env.getMeta().put("roomCode", roomCode);
        return env;
    }
}
