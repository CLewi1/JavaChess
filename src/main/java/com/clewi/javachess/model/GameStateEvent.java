package com.clewi.javachess.model;

import com.clewi.javachess.game.GameManager;

public class GameStateEvent {
    private final GameManager source;
    private final GameState gameState;

    public GameStateEvent(GameManager source) {
        this.source = source;
        this.gameState = source.getGameState();
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameManager getSource() {
        return source;
    }
}
