package com.clewi.javachess.game;


import org.junit.jupiter.api.Test;

public class GameManagerDebugTest {
    
    @Test
    public void debugAIBehavior() {
        GameManager gameManager = new GameManager();
        
        System.out.println("=== Initial State ===");
        System.out.println("AI Enabled: " + gameManager.isAIEnabled());
        System.out.println("AI Info: " + gameManager.getAIInfo());
        System.out.println("Current Player White: " + gameManager.getCurrentPlayer().isWhite());
        System.out.println("Is AI Turn: " + gameManager.isAITurn());
        
        System.out.println("\n=== Configure AI as White ===");
        gameManager.configureAI(true, true);
        System.out.println("AI Enabled: " + gameManager.isAIEnabled());
        System.out.println("AI Info: " + gameManager.getAIInfo());
        System.out.println("Current Player White: " + gameManager.getCurrentPlayer().isWhite());
        System.out.println("Is AI Turn: " + gameManager.isAITurn());
        
        System.out.println("\n=== Try AI Move ===");
        boolean success = gameManager.makeAIMove();
        System.out.println("AI Move Success: " + success);
        if (success) {
            System.out.println("Current Player after move White: " + gameManager.getCurrentPlayer().isWhite());
            System.out.println("Is AI Turn after move: " + gameManager.isAITurn());
        }
        
        System.out.println("\n=== Configure AI as Black ===");
        gameManager.configureAI(true, false);
        System.out.println("AI Enabled: " + gameManager.isAIEnabled());
        System.out.println("AI Info: " + gameManager.getAIInfo());
        System.out.println("Current Player White: " + gameManager.getCurrentPlayer().isWhite());
        System.out.println("Is AI Turn: " + gameManager.isAITurn());
        
        System.out.println("\n=== Try AI Move as Black (should fail) ===");
        success = gameManager.makeAIMove();
        System.out.println("AI Move Success: " + success);
    }
}