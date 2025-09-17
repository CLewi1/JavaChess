package com.clewi.javachess.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameManagerAIIntegrationTest {
    private GameManager gameManager;

    @BeforeEach
    public void setUp() {
        gameManager = new GameManager();
    }

    @Test
    public void testAIConfigurationBasic() {
        // Test initial state
        assertFalse(gameManager.isAIEnabled());
        assertNull(gameManager.getAIInfo()); // Returns null when disabled
        
        // Enable AI as black
        gameManager.configureAI(true, false);
        assertTrue(gameManager.isAIEnabled());
        assertTrue(gameManager.getAIInfo().contains("GreedyAI"));
        
        // Disable AI
        gameManager.configureAI(false, true);
        assertFalse(gameManager.isAIEnabled());
        assertNull(gameManager.getAIInfo()); // Returns null when disabled
    }

    @Test
    public void testAITurnDetectionWithStartingPosition() {
        // Starting position should have white to move
        // Check current player first
        assertTrue(gameManager.getCurrentPlayer().isWhite()); // Should be white's turn at start
        
        // Configure AI as white
        gameManager.configureAI(true, false);
        
        // Should be AI's turn (white's turn at start, AI is white)
        assertTrue(gameManager.isAITurn());
        
        // Reconfigure AI as black
        gameManager.configureAI(true, true);
        
        // Should not be AI's turn (still white's turn at start, but AI is black)
        assertFalse(gameManager.isAITurn());
    }

    @Test
    public void testMakeAIMoveWithStartingPosition() {
        // Configure AI as white (starting player)
        gameManager.configureAI(true, false);
        
        // Should be able to make a move from starting position
        boolean success = gameManager.makeAIMove();
        assertTrue(success);
        
        // After AI move, should no longer be AI's turn
        assertFalse(gameManager.isAITurn());
    }

    @Test
    public void testMakeAIMoveWhenNotAITurn() {
        // Configure AI as black but it's white's turn
        gameManager.configureAI(true, true);
        
        // Should fail because it's not AI's turn
        boolean success = gameManager.makeAIMove();
        assertFalse(success);
    }

    @Test
    public void testMakeAIMoveWhenAIDisabled() {
        // AI disabled
        gameManager.configureAI(false, true);
        
        // Should fail because AI is disabled
        boolean success = gameManager.makeAIMove();
        assertFalse(success);
    }

    @Test
    public void testMultipleAIConfigurations() {
        // Test changing AI configuration multiple times
        assertFalse(gameManager.isAIEnabled());
        
        // Enable as white
        gameManager.configureAI(true, false);
        assertTrue(gameManager.isAIEnabled());
        assertTrue(gameManager.isAITurn()); // White's turn at start
        
        // Switch to black
        gameManager.configureAI(true, true);
        assertTrue(gameManager.isAIEnabled());
        assertFalse(gameManager.isAITurn()); // Still white's turn, but AI is black now
        
        // Disable AI
        gameManager.configureAI(false, true);
        assertFalse(gameManager.isAIEnabled());
        assertFalse(gameManager.isAITurn());
    }

    @Test
    public void testAIMoveDoesNotCrash() {
        // Test that AI move execution doesn't throw exceptions
        gameManager.configureAI(true, true);
        
        assertDoesNotThrow(() -> {
            gameManager.makeAIMove();
        });
    }

    @Test
    public void testAIIntegrationWithGameState() {
        // Test that AI works with game state management
        gameManager.configureAI(true, true);
        
        // Should start in PLAYING state
        assertNotNull(gameManager.getGameState());
        
        // Making AI move should not break game state
        gameManager.makeAIMove();
        assertNotNull(gameManager.getGameState());
    }

    @Test
    public void testAIInfoConsistency() {
        // Test that AI info is consistent
        assertNull(gameManager.getAIInfo()); // Returns null when disabled
        
        gameManager.configureAI(true, true);
        assertTrue(gameManager.getAIInfo().contains("GreedyAI"));
        
        gameManager.configureAI(true, false);
        assertTrue(gameManager.getAIInfo().contains("GreedyAI"));
        
        gameManager.configureAI(false, false);
        assertNull(gameManager.getAIInfo()); // Returns null when disabled
    }

    @Test
    public void testGetCurrentPlayerWithAI() {
        // Test that getCurrentPlayer works with AI
        assertNotNull(gameManager.getCurrentPlayer());
        
        gameManager.configureAI(true, true);
        assertNotNull(gameManager.getCurrentPlayer());
        
        // After making a move, current player should change
        if (gameManager.makeAIMove()) {
            assertNotNull(gameManager.getCurrentPlayer());
        }
    }
}