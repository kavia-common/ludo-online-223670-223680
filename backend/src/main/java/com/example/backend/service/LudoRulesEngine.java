package com.example.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

/**
 * Ludo rules engine providing basic validation and move application.
 * This MVP uses a simplified track model and token positions:
 * - "HOME" means in yard; a roll of 6 can move to "0".
 * - numeric string (e.g., "23") is track position.
 * - "FINISHED" when token reaches end.
 *
 * PUBLIC_INTERFACE
 */
public class LudoRulesEngine {

    private static final int TRACK_LENGTH = 52; // standard outer track
    private static final int TOKENS_PER_PLAYER = 4;
    private static final Set<String> COLORS = Set.of("RED", "GREEN", "BLUE", "YELLOW");

    private final ObjectMapper mapper = new ObjectMapper();

    // PUBLIC_INTERFACE
    /**
     * Build an initial board state map.
     *
     * @param playerColors list of players by color
     * @param firstTurnColor color whose turn starts
     * @return map representing board state
     */
    public Map<String, Object> initialBoardState(List<String> playerColors, String firstTurnColor) {
        Map<String, Object> board = new LinkedHashMap<>();
        board.put("currentTurn", firstTurnColor);
        board.put("lastDice", null);
        Map<String, Object> tokens = new LinkedHashMap<>();
        for (String color : playerColors) {
            List<String> pos = new ArrayList<>(TOKENS_PER_PLAYER);
            for (int i = 0; i < TOKENS_PER_PLAYER; i++) pos.add("HOME");
            tokens.put(color, pos);
        }
        board.put("tokens", tokens);
        return board;
    }

    // PUBLIC_INTERFACE
    /**
     * Compute valid tokens a player can move for a dice roll.
     *
     * @param board board map
     * @param color player color
     * @param dice dice value 1..6
     * @return list of token indices that can be moved
     */
    public List<Integer> validMoves(Map<String, Object> board, String color, int dice) {
        List<Integer> valid = new ArrayList<>();
        Map<String, Object> tokens = tokens(board);
        @SuppressWarnings("unchecked")
        List<String> my = (List<String>) tokens.get(color);
        if (my == null) return valid;

        for (int i = 0; i < my.size(); i++) {
            String pos = my.get(i);
            if ("FINISHED".equals(pos)) continue;
            if ("HOME".equals(pos)) {
                if (dice == 6) valid.add(i);
            } else {
                // simple: moving forward within track; no home stretch modeling for MVP
                int p = Integer.parseInt(pos);
                int np = (p + dice) % TRACK_LENGTH;
                // for simplicity always valid; capture is resolved later
                valid.add(i);
            }
        }
        return valid;
    }

    // PUBLIC_INTERFACE
    /**
     * Apply a token move and mutate the board map; also resolve captures and finished state.
     * Returns whether player gets extra turn (on six).
     */
    public boolean applyMove(Map<String, Object> board, String color, int tokenIndex, int dice) {
        Map<String, Object> tokens = tokens(board);
        @SuppressWarnings("unchecked")
        List<String> my = (List<String>) tokens.get(color);
        String pos = my.get(tokenIndex);
        String newPos;
        if ("HOME".equals(pos)) {
            if (dice != 6) {
                throw new IllegalStateException("Cannot leave HOME without a 6");
            }
            newPos = "0";
        } else if ("FINISHED".equals(pos)) {
            throw new IllegalStateException("Token already finished");
        } else {
            int p = Integer.parseInt(pos);
            int np = (p + dice) % TRACK_LENGTH;
            // MVP does not include exact finish; keep on track looping.
            newPos = Integer.toString(np);
        }
        my.set(tokenIndex, newPos);

        // resolve captures: if any opponent token shares same numeric position, send it HOME
        if (!"HOME".equals(newPos) && !"FINISHED".equals(newPos)) {
            for (String otherColor : tokens.keySet()) {
                if (otherColor.equals(color)) continue;
                @SuppressWarnings("unchecked")
                List<String> otherList = (List<String>) tokens.get(otherColor);
                for (int i = 0; i < otherList.size(); i++) {
                    String op = otherList.get(i);
                    if (op.equals(newPos)) {
                        // simple capture rule: send opponent token to HOME
                        otherList.set(i, "HOME");
                    }
                }
            }
        }

        board.put("lastDice", dice);
        return dice == 6;
    }

    // PUBLIC_INTERFACE
    /**
     * Change the current turn to the next player in order.
     *
     * @param board map
     * @param turnOrder ordered list of colors
     * @param extraTurn true if same player continues
     */
    public void advanceTurn(Map<String, Object> board, List<String> turnOrder, boolean extraTurn) {
        String current = (String) board.get("currentTurn");
        if (extraTurn) {
            board.put("currentTurn", current);
            return;
        }
        int idx = turnOrder.indexOf(current);
        if (idx < 0) idx = 0;
        String next = turnOrder.get((idx + 1) % turnOrder.size());
        board.put("currentTurn", next);
    }

    // PUBLIC_INTERFACE
    /**
     * Parse Json string into board map.
     */
    public Map<String, Object> parseBoard(String json) {
        try {
            if (json == null || json.isBlank()) return new HashMap<>();
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse board json", e);
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Serialize board map to json.
     */
    public String toJson(Map<String, Object> board) {
        try {
            return mapper.writeValueAsString(board);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize board json", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> tokens(Map<String, Object> board) {
        Object t = board.get("tokens");
        if (t instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        Map<String, Object> newMap = new LinkedHashMap<>();
        board.put("tokens", newMap);
        return newMap;
    }

    // PUBLIC_INTERFACE
    /**
     * Normalize color string to canonical UPPER form if valid.
     *
     * @param color input
     * @return normalized
     */
    public String normalizeColor(String color) {
        if (color == null) return null;
        String up = color.trim().toUpperCase();
        return COLORS.contains(up) ? up : color;
    }
}
