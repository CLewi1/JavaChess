package com.clewi.javachess.ui;

import com.clewi.javachess.game.GameManager;
import com.clewi.javachess.game.GameStateEvent;
import com.clewi.javachess.game.GameStateObserver;
import com.clewi.javachess.model.*;
import com.clewi.javachess.util.DebugUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class GameScreen extends JFrame implements GameStateObserver, GameController {
    private BoardPanel boardPanel;
    private StatusPanel statusPanel;
    private GameManager gameManager;
    private Timer swingClockTimer;
    private ActionListener backToHomeListener;
    
    public GameScreen() {
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
    
    public void setBackToHomeListener(ActionListener listener) {
        this.backToHomeListener = listener;
    }
    
    private void initializeComponents() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Don't exit, just hide
        setLayout(new BorderLayout(10, 10));
        
        // Add a menu bar with a back button
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        
        JMenuItem backToHome = new JMenuItem("Back to Home");
        backToHome.addActionListener(e -> {
            if (backToHomeListener != null) {
                // Stop the clock timer when going back to home
                if (swingClockTimer != null) {
                    swingClockTimer.stop();
                }
                backToHomeListener.actionPerformed(e);
            }
        });
        
        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(e -> startNewGame());
        
        JMenuItem saveGame = new JMenuItem("Save Game");
        saveGame.addActionListener(e -> saveGame());
        
        JMenuItem loadGame = new JMenuItem("Load Game");
        loadGame.addActionListener(e -> loadGame());
        
        gameMenu.add(backToHome);
        gameMenu.addSeparator();
        gameMenu.add(newGame);
        gameMenu.addSeparator();
        gameMenu.add(saveGame);
        gameMenu.add(loadGame);
        
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
        
        boardPanel = new BoardPanel(gameManager);
        statusPanel = new StatusPanel(this);  // Pass reference to this GUI
        
        add(boardPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.EAST);
        
        pack();
        setLocationRelativeTo(null);
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
                JOptionPane.showMessageDialog(this, "The game is a draw.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            });
        } else if (state == GameState.TIMEOUT) {
            Player winner = event.getSource().getOppositePlayer();
            SwingUtilities.invokeLater(() -> {
                String message = "Time out! " + (winner.isWhite() ? "White" : "Black") + " wins on time!";
                JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }
    
    private void showCheckmateDialog(Player winner) {
        String message = "Checkmate! " + (winner.isWhite() ? "White" : "Black") + " wins!";
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showStalemateDialog() {
        JOptionPane.showMessageDialog(this, "Stalemate! The game is a draw.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
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
        
        // Restart the timer
        if (swingClockTimer != null) {
            swingClockTimer.start();
        }
    }
    
    public void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Chess Game Files (*.chess)", "chess"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Make sure file has .chess extension
            if (!fileToSave.getName().toLowerCase().endsWith(".chess")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".chess");
            }
            
            // Confirm if file exists
            if (fileToSave.exists()) {
                int response = JOptionPane.showConfirmDialog(this,
                        "The file already exists. Do you want to overwrite it?", 
                        "Confirm Overwrite", JOptionPane.YES_NO_OPTION);
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Save the game
            boolean success = gameManager.saveGame(fileToSave);
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Game saved successfully", 
                        "Save Game", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
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
        
        int userSelection = fileChooser.showOpenDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            
            // Load the game
            boolean success = gameManager.loadGame(fileToLoad);
            if (success) {
                boardPanel.refresh();
                statusPanel.updateStatus(gameManager.getGameState());
                statusPanel.setTurn(gameManager.getCurrentPlayer().isWhite());
                statusPanel.updateMoveHistory(gameManager.getDisplayMoveHistory()); // Update move history display
                JOptionPane.showMessageDialog(this, 
                        "Game loaded successfully", 
                        "Load Game", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
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
            JOptionPane.showMessageDialog(this, 
                    "No moves to undo", 
                    "Undo Move", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void showScreen() {
        setVisible(true);
    }
    
    public void hideScreen() {
        setVisible(false);
    }
}