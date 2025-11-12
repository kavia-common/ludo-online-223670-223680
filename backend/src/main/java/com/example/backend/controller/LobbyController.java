package com.example.backend.controller;

import com.example.backend.dto.PlayerDTO;
import com.example.backend.dto.RoomDTO;
import com.example.backend.dto.actions.CreateRoomRequest;
import com.example.backend.dto.actions.JoinRequest;
import com.example.backend.dto.actions.ReadyRequest;
import com.example.backend.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Lobby REST endpoints for rooms lifecycle.
 *
 * PUBLIC_INTERFACE
 */
@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Lobby", description = "Room lifecycle endpoints")
@Validated
public class LobbyController {

    private final RoomService roomService;

    public LobbyController(RoomService roomService) {
        this.roomService = roomService;
    }

    // PUBLIC_INTERFACE
    /**
     * Create a room.
     *
     * @param req CreateRoomRequest
     * @return RoomDTO
     */
    @PostMapping
    @Operation(summary = "Create room", description = "Creates a new room and returns its code")
    public RoomDTO create(@Valid @RequestBody CreateRoomRequest req) {
        return roomService.createRoom(req != null ? req.getName() : null);
    }

    // PUBLIC_INTERFACE
    /**
     * List rooms.
     *
     * @return list of RoomDTO
     */
    @GetMapping
    @Operation(summary = "List rooms", description = "Returns a list of rooms")
    public List<RoomDTO> list() {
        return roomService.listRooms();
    }

    // PUBLIC_INTERFACE
    /**
     * Join a room.
     *
     * @param code room code
     * @param req join request
     * @return PlayerDTO for joined player
     */
    @PostMapping("/{code}/join")
    @Operation(summary = "Join room", description = "Join a room by code")
    public PlayerDTO join(@PathVariable("code") String code, @Valid @RequestBody JoinRequest req) {
        return roomService.joinRoom(code, req.getName(), req.getPreferredColor());
    }

    // PUBLIC_INTERFACE
    /**
     * Leave a room.
     *
     * @param code room code
     * @param req ReadyRequest reused for color field
     */
    @PostMapping("/{code}/leave")
    @Operation(summary = "Leave room", description = "Leave a room by code and color")
    public void leave(@PathVariable("code") String code, @Valid @RequestBody ReadyRequest req) {
        roomService.leaveRoom(code, req.getColor());
    }

    // PUBLIC_INTERFACE
    /**
     * Toggle ready.
     */
    @PostMapping("/{code}/ready")
    @Operation(summary = "Ready toggle", description = "Toggle ready for player color (MVP no persistence)")
    public void ready(@PathVariable("code") String code, @Valid @RequestBody ReadyRequest req) {
        roomService.ready(code, req.getColor(), req.isReady());
    }

    // PUBLIC_INTERFACE
    /**
     * Start the game in the room.
     */
    @PostMapping("/{code}/start")
    @Operation(summary = "Start game", description = "Start game for a room and initialize state if needed")
    public void start(@PathVariable("code") String code) {
        roomService.start(code);
    }
}
