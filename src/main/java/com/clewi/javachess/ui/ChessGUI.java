package com.clewi.javachess.ui;

import com.clewi.javachess.game.GameManager;
import com.clewi.javachess.game.GameStateEvent;
import com.clewi.javachess.game.GameStateObserver;
import com.clewi.javachess.model.*;
import com.clewi.javachess.util.DebugUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class ChessGUI implements GameStateObserver {
    private JFrame mainFrame;
    private BoardPanel boardPanel;
    private StatusPanel statusPanel;
    private GameManager gameManager;
    private Timer swingClockTimer;
    
    public ChessGUI() {
        gameManager = new GameManager();
        gameManager.initClocks(300, 2);
        DebugUtils.logImportant("Clocks initialized to 5 minutes with 2 second increment.");
        
        // Register this as an observer for game state changes
        gameManager.registerObserver(this);
        
        initializeComponents();

            swingClockTimer = new Timer(1000, e -> {
            if (gameManager.isClockEnabled()) {
                boolean timeout = gameManager.tick(); // decrements active clock
                // update the labels in StatusPanel
                statusPanel.updateTimers(gameManager.getWhiteSecondsRemaining(), gameManager.getBlackSecondsRemaining());
                if (timeout) {
                    // stop timer when timeout occurs (gameManager.tick() should have updated game state)
                    swingClockTimer.stop();
                    // optionally show dialog - handled in onGameStateChanged observer too
                }
            } else {
                // still update labels (shows --:--)
                statusPanel.updateTimers(gameManager.getWhiteSecondsRemaining(), gameManager.getBlackSecondsRemaining());
            }
        });
        swingClockTimer.start();
    }
    
    private void initializeComponents() {
        mainFrame = new JFrame("Chess Game");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout(10, 10));
        
        boardPanel = new BoardPanel(gameManager);
        statusPanel = new StatusPanel(this);  // Pass reference to this GUI
        
        mainFrame.add(boardPanel, BorderLayout.CENTER);
        mainFrame.add(statusPanel, BorderLayout.EAST);
        
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    @Override
    public void onGameStateChanged(GameStateEvent event) {
        boardPanel.refresh();
        statusPanel.updateStatus(event.getGameState());
        statusPanel.setTurn(event.getSource().getCurrentPlayer().isWhite());
        statusPanel.updateMoveHistory(gameManager.getDisplayMoveHistory());

        // Check for checkmate or stalemate
        GameState state = event.getGameState();
        if (state == GameState.CHECKMATE) {
            Player winner = event.getSource().getOppositePlayer();
            SwingUtilities.invokeLater(() -> showCheckmateDialog(winner));
        } else if (state == GameState.STALEMATE) {
            SwingUtilities.invokeLater(() -> showStalemateDialog());
        } else if (state == GameState.DRAW) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(mainFrame, "The game is a draw.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            });
        } else if (state == GameState.TIMEOUT) {
            Player winner = event.getSource().getOppositePlayer();
            SwingUtilities.invokeLater(() -> {
                String message = "Time out! " + (winner.isWhite() ? "White" : "Black") + " wins on time!";
                JOptionPane.showMessageDialog(mainFrame, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            });
        }

    }
    
    private void showCheckmateDialog(Player winner) {
        String message = "Checkmate! " + (winner.isWhite() ? "White" : "Black") + " wins!";
        JOptionPane.showMessageDialog(mainFrame, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showStalemateDialog() {
        JOptionPane.showMessageDialog(mainFrame, "Stalemate! The game is a draw.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void startNewGame() {
        gameManager.resetGame();
        gameManager.initClocks(300, 2);
        boardPanel.refresh();
        statusPanel.updateStatus(GameState.PLAYING);
        statusPanel.setTurn(true);

        // Reset move history display
        Board.setMoveHistory(new ArrayList<>());
        statusPanel.updateMoveHistory(gameManager.getDisplayMoveHistory());
    }
    
    public void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Chess Game Files (*.chess)", "chess"));
        
        int userSelection = fileChooser.showSaveDialog(mainFrame);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Make sure file has .chess extension
            if (!fileToSave.getName().toLowerCase().endsWith(".chess")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".chess");
            }
            
            // Confirm if file exists
            if (fileToSave.exists()) {
                int response = JOptionPane.showConfirmDialog(mainFrame,
                        "The file already exists. Do you want to overwrite it?", 
                        "Confirm Overwrite", JOptionPane.YES_NO_OPTION);
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Save the game
            boolean success = gameManager.saveGame(fileToSave);
            if (success) {
                JOptionPane.showMessageDialog(mainFrame, 
                        "Game saved successfully", 
                        "Save Game", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, 
                        "Failed to save game", 
                        "Save Game", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Chess Game Files (*.chess)", "chess"));
        
        int userSelection = fileChooser.showOpenDialog(mainFrame);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            
            // Load the game
            boolean success = gameManager.loadGame(fileToLoad);
            if (success) {
                boardPanel.refresh();
                statusPanel.updateStatus(gameManager.getGameState());
                statusPanel.setTurn(gameManager.getCurrentPlayer().isWhite());
                statusPanel.updateMoveHistory(gameManager.getDisplayMoveHistory()); // Update move history display
                JOptionPane.showMessageDialog(mainFrame, 
                        "Game loaded successfully", 
                        "Load Game", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, 
                        "Failed to load game", 
                        "Load Game", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void undoMove() {
        boolean success = gameManager.undoMove();
        if (success) {
            boardPanel.refresh();
            statusPanel.updateStatus(gameManager.getGameState());
            statusPanel.setTurn(gameManager.getCurrentPlayer().isWhite());
            statusPanel.updateMoveHistory(gameManager.getDisplayMoveHistory());
        } else {
            JOptionPane.showMessageDialog(mainFrame, 
                    "No moves to undo", 
                    "Undo Move", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
