package com.clewi.javachess.model;

public interface GameStateObserver {
    void onGameStateChanged(GameStateEvent event);
}
