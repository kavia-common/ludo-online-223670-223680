package com.example.backend.dto.actions;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Token move request.
 *
 * PUBLIC_INTERFACE
 */
public class MoveRequest {
    @NotBlank
    private String color;

    @Min(0)
    @Max(3)
    private int tokenIndex;

    private Integer dice; // optional - if client passes

    public String getColor() {
        return color;
    }

    public MoveRequest setColor(String color) {
        this.color = color;
        return this;
    }

    public int getTokenIndex() {
        return tokenIndex;
    }

    public MoveRequest setTokenIndex(int tokenIndex) {
        this.tokenIndex = tokenIndex;
        return this;
    }

    public Integer getDice() {
        return dice;
    }

    public MoveRequest setDice(Integer dice) {
        this.dice = dice;
        return this;
    }
}
