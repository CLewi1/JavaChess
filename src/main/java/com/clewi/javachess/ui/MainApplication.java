package com.clewi.javachess.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApplication {
    private HomeScreen homeScreen;
    private GameScreen gameScreen;
    
    public MainApplication() {
        initializeScreens();
        setupNavigation();
        showHomeScreen();
    }
    
    private void initializeScreens() {
        homeScreen = new HomeScreen();
        gameScreen = new GameScreen();
    }
    
    private void setupNavigation() {
        // Set up home screen navigation to game screen
        homeScreen.setGameStartListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String gameMode = e.getActionCommand();
                startGame(gameMode);
            }
        });
        
        // Set up game screen navigation back to home screen
        gameScreen.setBackToHomeListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHomeScreen();
            }
        });
    }
    
    private void startGame(String gameMode) {
        homeScreen.hideScreen();
        
        gameScreen.setGameMode(gameMode);
        gameScreen.startNewGame();
        gameScreen.showScreen();
        
        // TODO: In the future, implement different logic for PVP vs PVAI
        if ("PVP".equals(gameMode)) {
            // Player vs Player mode
            System.out.println("Starting Player vs Player game");
        } else if ("PVAI".equals(gameMode)) {
            // Player vs AI mode 
            System.out.println("Starting Player vs AI game");
            // TODO: Initialize AI opponent
        }
    }
    
    private void showHomeScreen() {
        if (gameScreen != null) {
            gameScreen.hideScreen();
        }
        homeScreen.showScreen();
    }
    
    public void shutdown() {
        if (homeScreen != null) {
            homeScreen.dispose();
        }
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        System.exit(0);
    }
}