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
    
    // Individual timer panels
    private JLabel whiteTimerLabel;
    private JLabel blackTimerLabel;
    
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
                // update the timer labels
                updateTimerLabels();
                if (timeout) {
                    // stop timer when timeout occurs (gameManager.tick() should have updated game state)
                    swingClockTimer.stop();
                    // optionally show dialog - handled in onGameStateChanged observer too
                }
            } else {
                // still update labels (shows --:--)
                updateTimerLabels();
            }
        });
        swingClockTimer.start();
    }
    
    public void setBackToHomeListener(ActionListener listener) {
        this.backToHomeListener = listener;
    }
    
    private void initializeComponents() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                g2d.setPaint(new Color(48,46,43));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        
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
        
        // Create timer panels
        createTimerPanels();
        
        // Create board area with timers positioned around it
        JPanel boardArea = createBoardAreaWithTimers();
        
        // Create a centered container that holds both board area and status panel
        JPanel gameContainer = createCenteredGameContainer(boardArea);
        
        mainPanel.add(gameContainer, BorderLayout.CENTER);
        
        // Add the main panel to the frame
        add(mainPanel, BorderLayout.CENTER);
        
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
        
        // Update timer displays and restart the timer
        updateTimerLabels();
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
    
    private void createTimerPanels() {
        // Create white timer (top-left) - Black pieces start at top
        whiteTimerLabel = new JLabel("05:00", SwingConstants.CENTER);
        whiteTimerLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        whiteTimerLabel.setForeground(Color.BLACK);
        whiteTimerLabel.setBackground(new Color(255,255,255));
        whiteTimerLabel.setOpaque(true);

        // Create black timer (bottom-right) - White pieces start at bottom
        blackTimerLabel = new JLabel("05:00", SwingConstants.CENTER);
        blackTimerLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        blackTimerLabel.setForeground(Color.WHITE);
        blackTimerLabel.setBackground(new Color(38,36,33));
        blackTimerLabel.setOpaque(true);
    }
    
    private JPanel createBoardAreaWithTimers() {
        JPanel boardArea = new JPanel();
        boardArea.setLayout(null); // Use absolute positioning
        boardArea.setOpaque(false);
        
        // Calculate board size
        int boardSize = 65 * 8; // SQUARE_SIZE * 8
        
        // Position the board in the center
        int boardX = 20;
        int boardY = 60;
        boardPanel.setBounds(boardX, boardY, boardSize, boardSize);
        
        // Position white timer bottom-right of board
        whiteTimerLabel.setBounds(boardX + boardSize - 100, boardY + boardSize + 10, 100, 30);
        
        // Position black timer top-right of board
        blackTimerLabel.setBounds(boardX + boardSize - 100, boardY - 40, 100, 30);
        
        // Set preferred size for the container
        boardArea.setPreferredSize(new Dimension(boardSize + 40, boardSize + 120));
        
        // Add components
        boardArea.add(boardPanel);
        boardArea.add(whiteTimerLabel);
        boardArea.add(blackTimerLabel);
        
        return boardArea;
    }
    
    private JPanel createCenteredGameContainer(JPanel boardArea) {
        JPanel gameContainer = new JPanel();
        gameContainer.setOpaque(false);
        gameContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0)); // 30px spacing between components
        
        // Set status panel height to match board height and maintain its width
        int boardHeight = 65 * 8; // SQUARE_SIZE * 8 (chess board height)
        statusPanel.setPreferredSize(new Dimension(200, boardHeight)); // Keep width 200, height = board height
        
        // Add components with proper spacing
        gameContainer.add(boardArea);
        gameContainer.add(statusPanel);
        
        return gameContainer;
    }
    
    private void updateTimerLabels() {
        SwingUtilities.invokeLater(() -> {
            String whiteText = formatSecondsAsClock(gameManager.getWhiteSecondsRemaining());
            String blackText = formatSecondsAsClock(gameManager.getBlackSecondsRemaining());
            whiteTimerLabel.setText(whiteText);
            blackTimerLabel.setText(blackText);
        });
    }
    
    private String formatSecondsAsClock(int seconds) {
        int s = Math.max(0, seconds);
        int m = s / 60;
        int sec = s % 60;
        return String.format("%02d:%02d", m, sec);
    }
}