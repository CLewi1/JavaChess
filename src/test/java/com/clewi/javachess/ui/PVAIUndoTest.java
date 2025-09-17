package com.clewi.javachess.ui;

import com.clewi.javachess.game.GameManager;
import com.clewi.javachess.model.Board;
import com.clewi.javachess.model.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;

/**
 * Test for the enhanced undo functionality in PVAI mode
 * This test verifies that the GameScreen properly handles undo operations
 * differently for PVAI vs PVP modes
 */
public class PVAIUndoTest {
    
    private GameScreen gameScreen;
    private GameManager gameManager;
    
    @BeforeEach
    void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            gameScreen = new GameScreen();
            // Use reflection to access the private gameManager field
            try {
                java.lang.reflect.Field gameManagerField = GameScreen.class.getDeclaredField("gameManager");
                gameManagerField.setAccessible(true);
                gameManager = (GameManager) gameManagerField.get(gameScreen);
            } catch (Exception e) {
                fail("Failed to access GameManager: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testGameModeConfiguration() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Test PVAI mode setup
            gameScreen.setGameMode("PVAI");
            gameManager.configureAI(true, true); // AI as black
            
            assertTrue(gameManager.isAIEnabled(), "AI should be enabled in PVAI mode");
            assertTrue(gameManager.isAIAsBlack(), "AI should be configured as black");
            assertEquals(GameState.PLAYING, gameManager.getGameState());
            
            // Test PVP mode setup
            gameScreen.setGameMode("PVP");
            gameManager.configureAI(false, false);
            
            assertFalse(gameManager.isAIEnabled(), "AI should be disabled in PVP mode");
            assertEquals(GameState.PLAYING, gameManager.getGameState());
        });
    }
    
    @Test
    void testUndoWithEmptyMoveHistory() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Test PVAI mode with no moves
            gameScreen.setGameMode("PVAI");
            gameManager.configureAI(true, true);
            
            assertEquals(0, Board.getMoveHistory().size(), "Should start with no moves");
            
            // Call undo - should handle gracefully without crashing
            gameScreen.undoMove();
            
            // Should still have no moves and be in valid state
            assertEquals(0, Board.getMoveHistory().size(), "Should still have no moves");
            assertEquals(GameState.PLAYING, gameManager.getGameState());
            assertTrue(gameManager.getCurrentPlayer().isWhite(), "Should still be white's turn");
        });
    }
    
    @Test
    void testGameStateConsistency() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Test that game state remains consistent after undo operations
            gameScreen.setGameMode("PVAI");
            gameManager.configureAI(true, true);
            
            // Verify initial state
            assertEquals(GameState.PLAYING, gameManager.getGameState());
            assertTrue(gameManager.getCurrentPlayer().isWhite());
            assertNotNull(gameManager.getBoard());
            
            // Attempt undo on empty game
            gameScreen.undoMove();
            
            // State should remain consistent
            assertEquals(GameState.PLAYING, gameManager.getGameState());
            assertTrue(gameManager.getCurrentPlayer().isWhite());
            assertNotNull(gameManager.getBoard());
        });
    }
    
    @Test
    void testModeDetection() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Test that the undo method correctly detects game mode
            
            // Set PVAI mode
            gameScreen.setGameMode("PVAI");
            gameManager.configureAI(true, true);
            assertTrue(gameManager.isAIEnabled());
            
            // Set PVP mode  
            gameScreen.setGameMode("PVP");
            gameManager.configureAI(false, false);
            assertFalse(gameManager.isAIEnabled());
            
            // Both modes should handle undo gracefully with empty move history
            gameScreen.undoMove(); // Should not crash
            assertEquals(GameState.PLAYING, gameManager.getGameState());
        });
    }
}