package com.example.backend.dto.actions;

import jakarta.validation.constraints.NotBlank;

/**
 * Join room request DTO.
 *
 * PUBLIC_INTERFACE
 */
public class JoinRequest {
    @NotBlank
    private String name;

    private String preferredColor;

    public String getName() {
        return name;
    }

    public JoinRequest setName(String name) {
        this.name = name;
        return this;
    }

    public String getPreferredColor() {
        return preferredColor;
    }

    public JoinRequest setPreferredColor(String preferredColor) {
        this.preferredColor = preferredColor;
        return this;
    }
}
