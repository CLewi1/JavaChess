package com.clewi.javachess.game;

public interface GameStateObserver {
    void onGameStateChanged(GameStateEvent event);
}
