package com.example.backend.dto.actions;

import jakarta.validation.constraints.Size;

/**
 * Create room request DTO.
 *
 * PUBLIC_INTERFACE
 */
public class CreateRoomRequest {
    @Size(max = 100)
    private String name;

    public String getName() {
        return name;
    }

    public CreateRoomRequest setName(String name) {
        this.name = name;
        return this;
    }
}
