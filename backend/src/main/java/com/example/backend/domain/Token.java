package com.example.backend.domain;

import jakarta.persistence.*;

/**
 * Represents a single token/pawn belonging to a Player.
 *
 * PUBLIC_INTERFACE
 */
@Entity
@Table(name = "tokens", indexes = {
        @Index(name = "idx_token_player", columnList = "player_id")
})
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Player owner.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    /**
     * Token index (0..3) for a given player.
     */
    @Column(nullable = false)
    private int tokenIndex;

    /**
     * Position descriptor:
     * - "HOME" for home yard
     * - "FINISHED" when reached end
     * - or numeric track index as string, e.g. "23"
     */
    @Column(nullable = false, length = 32)
    private String position;

    public Token() {
    }

    public Token(Player player, int tokenIndex, String position) {
        this.player = player;
        this.tokenIndex = tokenIndex;
        this.position = position;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Token setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public int getTokenIndex() {
        return tokenIndex;
    }

    public Token setTokenIndex(int tokenIndex) {
        this.tokenIndex = tokenIndex;
        return this;
    }

    public String getPosition() {
        return position;
    }

    public Token setPosition(String position) {
        this.position = position;
        return this;
    }
}
