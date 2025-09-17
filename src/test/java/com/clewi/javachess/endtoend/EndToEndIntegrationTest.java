package com.clewi.javachess.endtoend;

import com.clewi.javachess.game.GameManager;
import com.clewi.javachess.ui.GameScreen;
import com.clewi.javachess.model.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;

/**
 * End-to-End Testing for Chess AI with Game Flow Integration
 * Tests complete game scenarios focusing on AI behavior, UI components, and game state management
 */
public class EndToEndIntegrationTest {
    
    private GameScreen gameScreen;
    private GameManager gameManager;
    
    @BeforeEach
    void setUp() throws Exception {
        // Initialize the game screen directly for testing
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
        
        // Wait for UI to be fully initialized
        Thread.sleep(100);
    }
    
    @Test
    @Timeout(30)
    void testGameScreenInitialization() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Test that GameScreen initializes properly with GameManager
            assertNotNull(gameScreen, "GameScreen should be initialized");
            assertNotNull(gameManager, "GameManager should be accessible");
            assertEquals(GameState.PLAYING, gameManager.getGameState(), 
                "Game should start in PLAYING state");
            assertNotNull(gameManager.getBoard(), "Board should be initialized");
            assertTrue(gameManager.getCurrentPlayer().isWhite(), 
                "White should start first");
        });
    }
    
    @Test
    @Timeout(10)
    void testAIIntegrationWithGameScreen() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Test AI integration without waiting for actual moves
            gameManager.configureAI(true, false);
            assertTrue(gameManager.isAIEnabled(), "AI should be enabled");
            assertTrue(gameManager.isAITurn(), "Should be AI's turn immediately");
            
            // Test switching AI configuration
            gameManager.configureAI(true, true);
            assertTrue(gameManager.isAIEnabled(), "AI should still be enabled");
            assertFalse(gameManager.isAITurn(), "Should not be AI's turn (black)");
            
            // Verify game state remains valid
            assertEquals(GameState.PLAYING, gameManager.getGameState(), 
                "Game should be in PLAYING state");
            assertNotNull(gameManager.getBoard(), "Board should be accessible");
        });
    }
    
    @Test
    @Timeout(20)
    void testGameStateManagement() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Test various game state scenarios
            assertEquals(GameState.PLAYING, gameManager.getGameState(), 
                "Should start in PLAYING state");
            
            // Test AI configuration changes
            gameManager.configureAI(true, true);
            assertTrue(gameManager.isAIEnabled(), "AI should be enabled");
            
            gameManager.configureAI(false, false);
            assertFalse(gameManager.isAIEnabled(), "AI should be disabled");
            
            // Test board state
            assertNotNull(gameManager.getBoard(), "Board should exist");
            int totalPieces = gameManager.getBoard().getPieces(true).size() + 
                              gameManager.getBoard().getPieces(false).size();
            assertEquals(32, totalPieces, "Should have 32 pieces at start");
        });
    }
    
    @Test
    void testSaveLoadWithGameScreen() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                // Configure AI and let it make a move
                gameManager.configureAI(true, false);
                
                // Wait for AI to move
                int attempts = 0;
                while (gameManager.isAITurn() && attempts < 50) {
                    Thread.sleep(100);
                    attempts++;
                }
                
                // Save the game state
                java.io.File saveFile = new java.io.File("test_gamescreen_save.chess");
                boolean saveSuccess = gameManager.saveGame(saveFile);
                assertTrue(saveSuccess, "Game should save successfully");
                
                // Create new GameManager and test loading
                GameManager newManager = new GameManager();
                boolean loadSuccess = newManager.loadGame(saveFile);
                assertTrue(loadSuccess, "Game should load successfully");
                
                // Verify the loaded state matches (compare player properties, not objects)
                assertEquals(gameManager.getCurrentPlayer().isWhite(), newManager.getCurrentPlayer().isWhite(), 
                    "Current player should match after load");
                assertEquals(gameManager.getGameState(), newManager.getGameState(), 
                    "Game state should match after load");
                
                // Cleanup
                saveFile.delete();
            } catch (Exception e) {
                fail("Save/load with GameScreen failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    @Timeout(15)
    void testAIConfigurationChanges() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Test that AI configuration can be changed dynamically
            assertFalse(gameManager.isAIEnabled(), "AI should start disabled");
            
            // Enable AI as white
            gameManager.configureAI(true, false);
            assertTrue(gameManager.isAIEnabled(), "AI should be enabled");
            assertTrue(gameManager.isAITurn(), "Should be AI's turn (white)");
            
            // Disable AI
            gameManager.configureAI(false, false);
            assertFalse(gameManager.isAIEnabled(), "AI should be disabled");
            
            // Enable AI as black
            gameManager.configureAI(true, true);
            assertTrue(gameManager.isAIEnabled(), "AI should be enabled as black");
            assertFalse(gameManager.isAITurn(), "Should not be AI's turn (black)");
        });
    }
}