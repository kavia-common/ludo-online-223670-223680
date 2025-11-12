package com.example.backend.service;

import com.example.backend.domain.GameState;
import com.example.backend.domain.Player;
import com.example.backend.domain.Room;
import com.example.backend.dto.PlayerDTO;
import com.example.backend.dto.RoomDTO;
import com.example.backend.repository.GameStateRepository;
import com.example.backend.repository.PlayerRepository;
import com.example.backend.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Room management service.
 *
 * PUBLIC_INTERFACE
 */
@Service
public class RoomService {
    private static final Logger log = LoggerFactory.getLogger(RoomService.class);
    private static final List<String> COLORS = List.of("RED", "GREEN", "BLUE", "YELLOW");

    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final GameStateRepository gameStateRepository;
    private final LudoRulesEngine rules;
    private final ObjectMapper mapper = new ObjectMapper();

    private final SecureRandom random = new SecureRandom();

    public RoomService(RoomRepository roomRepository,
                       PlayerRepository playerRepository,
                       GameStateRepository gameStateRepository) {
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
        this.gameStateRepository = gameStateRepository;
        this.rules = new LudoRulesEngine();
    }

    // PUBLIC_INTERFACE
    /**
     * Create a new room with unique code.
     *
     * @param name optional room display name
     * @return RoomDTO
     */
    @Transactional
    public RoomDTO createRoom(String name) {
        String code = generateCode();
        Room room = new Room(code, name);
        room = roomRepository.save(room);
        log.info("room created code={} name={}", code, name);
        return toDTO(room);
    }

    // PUBLIC_INTERFACE
    /**
     * List all rooms.
     */
    public List<RoomDTO> listRooms() {
        List<Room> all = roomRepository.findAll();
        List<RoomDTO> out = new ArrayList<>();
        for (Room r : all) out.add(toDTO(r));
        return out;
    }

    // PUBLIC_INTERFACE
    /**
     * Join room with name and optional preferred color.
     */
    @Transactional
    public PlayerDTO joinRoom(String roomCode, String name, String preferredColor) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new NoSuchElementException("Room not found"));
        if (!room.isActive()) throw new IllegalStateException("Room not active");

        String color = assignColor(room, preferredColor);
        int order = room.getPlayers().size();
        Player p = new Player(name, color, false, order);
        room.addPlayer(p);
        roomRepository.save(room);
        playerRepository.save(p);
        log.info("player joined room={} color={} name={}", roomCode, color, name);
        return toDTO(p);
    }

    // PUBLIC_INTERFACE
    /**
     * Leave room by color (simple MVP).
     */
    @Transactional
    public void leaveRoom(String roomCode, String color) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new NoSuchElementException("Room not found"));
        String up = color.toUpperCase();
        Optional<Player> playerOpt = room.getPlayers().stream().filter(pl -> up.equals(pl.getColor())).findFirst();
        if (playerOpt.isEmpty()) return;
        Player p = playerOpt.get();
        room.removePlayer(p);
        playerRepository.delete(p);
        log.info("player left room={} color={}", roomCode, up);
    }

    // PUBLIC_INTERFACE
    /**
     * Ready toggle placeholder (MVP: no persistence separate field).
     */
    public void ready(String roomCode, String color, boolean ready) {
        log.info("player ready-toggle room={} color={} ready={}", roomCode, color, ready);
    }

    // PUBLIC_INTERFACE
    /**
     * Start the game: initialize GameState if not present.
     *
     * @return true if started/exists
     */
    @Transactional
    public boolean start(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new NoSuchElementException("Room not found"));
        if (room.getPlayers().isEmpty()) throw new IllegalStateException("No players in room");
        if (room.getGameState() != null) return true;

        List<String> order = room.getPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getTurnOrder))
                .map(Player::getColor)
                .toList();
        String first = order.get(0);
        Map<String, Object> board = new LudoRulesEngine().initialBoardState(order, first);
        String json;
        try {
            json = mapper.writeValueAsString(board);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create initial board", e);
        }
        GameState gs = new GameState(room, json)
                .setCurrentTurnColor(first)
                .setLastDiceRoll(null);
        room.setGameState(gs);
        roomRepository.save(room);
        gameStateRepository.save(gs);
        log.info("game started room={} firstTurn={}", roomCode, first);
        return true;
    }

    // PUBLIC_INTERFACE
    /**
     * Get current Room entity by code.
     */
    public Room getRoomEntity(String roomCode) {
        return roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new NoSuchElementException("Room not found"));
    }

    private RoomDTO toDTO(Room room) {
        RoomDTO dto = new RoomDTO()
                .setRoomCode(room.getRoomCode())
                .setName(room.getName())
                .setActive(room.isActive())
                .setCreatedAt(room.getCreatedAt());
        List<PlayerDTO> players = new ArrayList<>();
        for (Player p : room.getPlayers()) players.add(toDTO(p));
        dto.setPlayers(players);
        return dto;
    }

    private PlayerDTO toDTO(Player p) {
        return new PlayerDTO()
                .setId(p.getId())
                .setName(p.getName())
                .setColor(p.getColor())
                .setAi(p.isAi())
                .setTurnOrder(p.getTurnOrder());
    }

    private String generateCode() {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        for (int attempt = 0; attempt < 100; attempt++) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                b.append(alphabet.charAt(random.nextInt(alphabet.length())));
            }
            String code = b.toString();
            if (!roomRepository.existsByRoomCode(code)) return code;
        }
        throw new IllegalStateException("Failed to generate room code");
    }

    private String assignColor(Room room, String preferred) {
        Set<String> used = new HashSet<>();
        for (Player p : room.getPlayers()) used.add(p.getColor());
        List<String> available = new ArrayList<>();
        for (String c : COLORS) if (!used.contains(c)) available.add(c);
        if (available.isEmpty()) throw new IllegalStateException("Room is full");
        if (preferred != null) {
            String up = preferred.toUpperCase();
            if (available.contains(up)) return up;
        }
        return available.get(0);
    }
}
