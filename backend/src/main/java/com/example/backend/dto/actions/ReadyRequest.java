package com.example.backend.dto.actions;

import jakarta.validation.constraints.NotBlank;

/**
 * Ready toggle request.
 *
 * PUBLIC_INTERFACE
 */
public class ReadyRequest {
    @NotBlank
    private String color;

    private boolean ready;

    public String getColor() {
        return color;
    }

    public ReadyRequest setColor(String color) {
        this.color = color;
        return this;
    }

    public boolean isReady() {
        return ready;
    }

    public ReadyRequest setReady(boolean ready) {
        this.ready = ready;
        return this;
    }
}
