package com.clewi.javachess.game;

import com.clewi.javachess.model.GameState;

public class GameStateEvent {
    private final GameManager source;
    
    public GameStateEvent(GameManager source) {
        this.source = source;
    }
    
    public GameManager getSource() {
        return source;
    }
    
    public GameState getGameState() {
        return source.getGameState();
    }
}
