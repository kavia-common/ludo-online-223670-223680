package com.example.backend.dto.actions;

import jakarta.validation.constraints.NotBlank;

/**
 * Dice roll request.
 *
 * PUBLIC_INTERFACE
 */
public class RollRequest {
    @NotBlank
    private String color;

    public String getColor() {
        return color;
    }

    public RollRequest setColor(String color) {
        this.color = color;
        return this;
    }
}
