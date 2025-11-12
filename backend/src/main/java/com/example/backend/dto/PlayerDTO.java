package com.example.backend.dto;

/**
 * Player info sent to clients.
 *
 * PUBLIC_INTERFACE
 */
public class PlayerDTO {
    private Long id;
    private String name;
    private String color;
    private boolean ai;
    private int turnOrder;

    public Long getId() {
        return id;
    }

    public PlayerDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PlayerDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getColor() {
        return color;
    }

    public PlayerDTO setColor(String color) {
        this.color = color;
        return this;
    }

    public boolean isAi() {
        return ai;
    }

    public PlayerDTO setAi(boolean ai) {
        this.ai = ai;
        return this;
    }

    public int getTurnOrder() {
        return turnOrder;
    }

    public PlayerDTO setTurnOrder(int turnOrder) {
        this.turnOrder = turnOrder;
        return this;
    }
}
