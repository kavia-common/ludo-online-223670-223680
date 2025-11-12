package com.example.backend.service;

import com.example.backend.domain.GameState;
import com.example.backend.domain.Room;
import com.example.backend.dto.GameEventEnvelope;
import com.example.backend.dto.GameStateDTO;
import com.example.backend.repository.GameStateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Game actions service. Authoritative game operations with simple validation.
 *
 * PUBLIC_INTERFACE
 */
@Service
public class GameService {
    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameStateRepository gameStateRepository;
    private final RoomService roomService;
    private final BroadcastService broadcastService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final LudoRulesEngine rules = new LudoRulesEngine();

    // room-level locks
    private final Map<String, Object> locks = new ConcurrentHashMap<>();

    public GameService(GameStateRepository gameStateRepository,
                       RoomService roomService,
                       BroadcastService broadcastService) {
        this.gameStateRepository = gameStateRepository;
        this.roomService = roomService;
        this.broadcastService = broadcastService;
    }

    private Object roomLock(String code) {
        return locks.computeIfAbsent(code, k -> new Object());
    }

    // PUBLIC_INTERFACE
    /**
     * Retrieve current GameStateDTO for a room.
     */
    @Transactional
    public GameStateDTO getState(String roomCode) {
        Room room = roomService.getRoomEntity(roomCode);
        GameState gs = room.getGameState();
        if (gs == null) {
            // Not started: initialize in-memory view
            return new GameStateDTO()
                    .setRoomCode(roomCode)
                    .setCurrentTurnColor(null)
                    .setLastDiceRoll(null)
                    .setBoard(Map.of())
                    .setUpdatedAt(Instant.now());
        }
        Map<String, Object> board = parseBoard(gs.getBoardStateJson());
        return toDTO(room.getRoomCode(), gs.getCurrentTurnColor(), gs.getLastDiceRoll(), board, gs.getUpdatedAt());
    }

    // PUBLIC_INTERFACE
    /**
     * Apply a dice roll. If valid, records lastDice but does not auto-advance turn until a move or pass.
     */
    @Transactional
    public GameStateDTO rollDice(String roomCode, String color) {
        synchronized (roomLock(roomCode)) {
            Room room = roomService.getRoomEntity(roomCode);
            ensureStarted(room);
            GameState gs = room.getGameState();
            Map<String, Object> board = parseBoard(gs.getBoardStateJson());
            String current = (String) board.get("currentTurn");
            String norm = rules.normalizeColor(color);
            if (!Objects.equals(current, norm)) {
                throw new IllegalStateException("Not your turn");
            }
            int dice = 1 + new Random().nextInt(6);
            board.put("lastDice", dice);
            gs.setLastDiceRoll(dice);
            gs.setBoardStateJson(toJson(board));
            gameStateRepository.save(gs);
            GameStateDTO dto = toDTO(room.getRoomCode(), (String) board.get("currentTurn"), dice, board, gs.getUpdatedAt());
            broadcastService.broadcast(roomCode, GameEventEnvelope.of("DiceRolled", dto, roomCode));
            log.info("dice rolled room={} color={} dice={}", roomCode, color, dice);
            return dto;
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Apply a move for a token index with current dice (or provided dice).
     */
    @Transactional
    public GameStateDTO moveToken(String roomCode, String color, int tokenIndex, Integer diceOverride) {
        synchronized (roomLock(roomCode)) {
            Room room = roomService.getRoomEntity(roomCode);
            ensureStarted(room);
            GameState gs = room.getGameState();
            Map<String, Object> board = parseBoard(gs.getBoardStateJson());
            String current = (String) board.get("currentTurn");
            String norm = rules.normalizeColor(color);
            if (!Objects.equals(current, norm)) {
                throw new IllegalStateException("Not your turn");
            }
            Integer dice = diceOverride != null ? diceOverride : (Integer) board.get("lastDice");
            if (dice == null) {
                throw new IllegalStateException("Roll dice first");
            }
            List<String> order = room.getPlayers().stream()
                    .sorted(Comparator.comparingInt(p -> p.getTurnOrder()))
                    .map(p -> p.getColor())
                    .toList();

            List<Integer> valid = rules.validMoves(board, norm, dice);
            if (!valid.contains(tokenIndex)) {
                throw new IllegalStateException("Invalid move");
            }
            boolean extra = rules.applyMove(board, norm, tokenIndex, dice);
            rules.advanceTurn(board, order, extra);

            gs.setCurrentTurnColor((String) board.get("currentTurn"));
            gs.setBoardStateJson(toJson(board));
            // if extra turn was taken due to 6, keep lastDice to allow move; else clear
            if (!extra) {
                board.put("lastDice", null);
                gs.setLastDiceRoll(null);
                gs.setBoardStateJson(toJson(board));
            }

            gameStateRepository.save(gs);
            GameStateDTO dto = toDTO(room.getRoomCode(), gs.getCurrentTurnColor(), (Integer) board.get("lastDice"), board, gs.getUpdatedAt());
            broadcastService.broadcast(roomCode, GameEventEnvelope.of("GameStateUpdated", dto, roomCode));
            log.info("move applied room={} color={} token={} extraTurn={}", roomCode, color, tokenIndex, extra);
            return dto;
        }
    }

    private void ensureStarted(Room room) {
        if (room.getGameState() == null) throw new IllegalStateException("Game not started");
    }

    private Map<String, Object> parseBoard(String json) {
        try {
            if (json == null || json.isBlank()) return new HashMap<>();
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Parse board failed", e);
        }
    }

    private String toJson(Map<String, Object> board) {
        try {
            return mapper.writeValueAsString(board);
        } catch (Exception e) {
            throw new IllegalStateException("Serialize board failed", e);
        }
    }

    private GameStateDTO toDTO(String roomCode, String currentTurn, Integer lastDice, Map<String, Object> board, Instant updated) {
        return new GameStateDTO()
                .setRoomCode(roomCode)
                .setCurrentTurnColor(currentTurn)
                .setLastDiceRoll(lastDice)
                .setBoard(board)
                .setUpdatedAt(updated);
    }
}
