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
    private String gameMode = "PVP"; // default to player vs player
    private Timer swingClockTimer;
    private ActionListener backToHomeListener;
    private boolean timersEnabled = true; // Track if timers are enabled
    
    // Individual timer panels
    private JLabel whiteTimerLabel;
    private JLabel blackTimerLabel;
    
    // Player name labels
    private JLabel whitePlayerLabel;
    private JLabel blackPlayerLabel;
    
    public GameScreen() {
        setMinimumSize(new Dimension(900, 700));
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

        JMenu viewMenu = new JMenu("View");

        // zoom size
        JMenuItem zoomIn = new JMenuItem("Zoom In");
        zoomIn.addActionListener(e -> {
            //boardPanel.setSquareSize(boardPanel.getSquareSize() + 5);
            boardPanel.revalidate();
            boardPanel.repaint();
        });

        JMenuItem zoomOut = new JMenuItem("Zoom Out");
        zoomOut.addActionListener(e -> {
            //boardPanel.setSquareSize(Math.max(20, boardPanel.getSquareSize() - 5));
            boardPanel.revalidate();
            boardPanel.repaint();
        });

        viewMenu.add(zoomIn);
        viewMenu.add(zoomOut);

        
        menuBar.add(gameMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
        
    boardPanel = new BoardPanel(gameManager);
    statusPanel = new StatusPanel(this);  // Pass reference to this GUI
    // Initialize title based on current game mode
    statusPanel.setGameModeTitle(gameMode);
        
        // Create board area (conditionally with or without timers)
        JPanel boardArea = createBoardArea();
        
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
        // Show game settings dialog
        GameSettingsDialog settingsDialog = new GameSettingsDialog(this);
        settingsDialog.setVisible(true);
        
        // If user canceled, don't start a new game
        if (!settingsDialog.isConfirmed()) {
            return;
        }
        
        // Update timer enabled state
        timersEnabled = settingsDialog.isTimerEnabled();
        
        gameManager.resetGame();
        
        // Configure clock based on user settings
        if (settingsDialog.isTimerEnabled()) {
            gameManager.initClocks(settingsDialog.getSecondsPerPlayer(), settingsDialog.getIncrementSeconds());
        } else {
            gameManager.disableClocks();
        }
        
        // Always rebuild the UI layout to ensure player names reflect current game mode
        refreshGameLayout();
        
        boardPanel.refresh();
        statusPanel.updateStatus(GameState.PLAYING);
        statusPanel.setTurn(true);

        // Reset move history display
        Board.setMoveHistory(new ArrayList<>());
        statusPanel.updateMoveHistory(gameManager.getDisplayMoveHistory());
        
        // Update timer displays and restart the timer
        if (timersEnabled) {
            updateTimerLabels();
        }
        if (swingClockTimer != null) {
            if (settingsDialog.isTimerEnabled()) {
                swingClockTimer.start();
            } else {
                swingClockTimer.stop();
            }
        }
        
        // Show game start message in status panel
        statusPanel.addGameStartMessage();
    }
    
    private void refreshGameLayout() {
        // Get the main panel and remove the old game container
        JPanel mainPanel = (JPanel) getContentPane().getComponent(0);
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                // Check if this is the game container (contains board and status)
                if (panel.getComponentCount() > 0 && panel.getLayout() instanceof FlowLayout) {
                    mainPanel.remove(panel);
                    break;
                }
            }
        }
        
        // Create new board area with current timer settings
        JPanel boardArea = createBoardArea();
        
        // Create a new centered container
        JPanel gameContainer = createCenteredGameContainer(boardArea);
        
        // Add the new container
        mainPanel.add(gameContainer, BorderLayout.CENTER);
        
        // Refresh the display
        mainPanel.revalidate();
        mainPanel.repaint();
        pack();
    }

    /**
     * Set the current game mode (e.g. "PVP" or "PVAI") and update UI labels accordingly.
     */
    public void setGameMode(String mode) {
        if (mode == null) return;
        this.gameMode = mode;
        if (statusPanel != null) {
            statusPanel.setGameModeTitle(mode);
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
    
    private JPanel createBoardArea() {
        // Always create player name labels
        createPlayerNameLabels();
        
        if (timersEnabled) {
            createTimerPanels();
            return createBoardAreaWithTimers();
        } else {
            return createBoardAreaWithoutTimers();
        }
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
    
    private void createPlayerNameLabels() {
        // Determine player names based on game mode
        String whitePlayerName, blackPlayerName;
        if ("PVAI".equals(gameMode)) {
            whitePlayerName = "Player";  // Human player (white pieces at bottom)
            blackPlayerName = "AI";      // AI player (black pieces at top)
        } else {
            whitePlayerName = "Player 1"; // White player
            blackPlayerName = "Player 2"; // Black player
        }
        
        // Create white player label (bottom-left, same line as white timer)
        whitePlayerLabel = new JLabel(whitePlayerName, SwingConstants.LEFT);
        whitePlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        whitePlayerLabel.setForeground(Color.WHITE);
        
        // Create black player label (top-left, same line as black timer)
        blackPlayerLabel = new JLabel(blackPlayerName, SwingConstants.LEFT);
        blackPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        blackPlayerLabel.setForeground(Color.WHITE);
    }
    
    private JPanel createBoardAreaWithoutTimers() {
        JPanel boardArea = new JPanel();
        boardArea.setLayout(null); // Use absolute positioning
        boardArea.setOpaque(false);
        
        // Calculate board size
        int boardSize = 65 * 8; // SQUARE_SIZE * 8
        
        // Position the board in the center (same positioning as with timers)
        int boardX = 120; 
        int boardY = 60; // Same vertical positioning as with timers
        boardPanel.setBounds(boardX, boardY, boardSize, boardSize);
        
        // Position player name labels aligned with left edge of board, mirroring where timers would be positioned
        blackPlayerLabel.setBounds(boardX, boardY - 40, 100, 30); // Above board - where black timer would be, aligned with left edge
        whitePlayerLabel.setBounds(boardX, boardY + boardSize + 10, 100, 30); // Below board - where white timer would be, aligned with left edge
        
        // Set preferred size for the container (same as with timers to prevent position shifts)
        boardArea.setPreferredSize(new Dimension(boardSize + 140, boardSize + 120));
        
        // Add components
        boardArea.add(boardPanel);
        boardArea.add(whitePlayerLabel);
        boardArea.add(blackPlayerLabel);
        
        return boardArea;
    }
    
    private JPanel createBoardAreaWithTimers() {
        JPanel boardArea = new JPanel();
        boardArea.setLayout(null); // Use absolute positioning
        boardArea.setOpaque(false);
        
        // Calculate board size
        int boardSize = 65 * 8; // SQUARE_SIZE * 8
        
        // Position the board in the center (more space for player names on left)
        int boardX = 120;
        int boardY = 60;
        boardPanel.setBounds(boardX, boardY, boardSize, boardSize);
        
        // Position white timer bottom-right of board
        whiteTimerLabel.setBounds(boardX + boardSize - 100, boardY + boardSize + 10, 100, 30);
        
        // Position black timer top-right of board
        blackTimerLabel.setBounds(boardX + boardSize - 100, boardY - 40, 100, 30);
        
        // Position player name labels aligned with left edge of board, mirroring the timer positions exactly
        blackPlayerLabel.setBounds(boardX, boardY - 40, 100, 30); // Above board - same Y as black timer, aligned with left edge
        whitePlayerLabel.setBounds(boardX, boardY + boardSize + 10, 100, 30); // Below board - same Y as white timer, aligned with left edge
        
        // Set preferred size for the container (wider to accommodate player names)
        boardArea.setPreferredSize(new Dimension(boardSize + 140, boardSize + 120));
        
        // Add components
        boardArea.add(boardPanel);
        boardArea.add(whiteTimerLabel);
        boardArea.add(blackTimerLabel);
        boardArea.add(whitePlayerLabel);
        boardArea.add(blackPlayerLabel);
        
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
        if (!timersEnabled || whiteTimerLabel == null || blackTimerLabel == null) {
            return; // Skip update if timers are disabled or not created
        }
        
        SwingUtilities.invokeLater(() -> {
            String whiteText = formatSecondsAsClock(gameManager.getWhiteSecondsRemaining());
            String blackText = formatSecondsAsClock(gameManager.getBlackSecondsRemaining());
            whiteTimerLabel.setText(whiteText);
            blackTimerLabel.setText(blackText);
        });
    }
    
    private String formatSecondsAsClock(Integer seconds) {
        if (seconds == null) {
            return "00:00";
        }
        int s = Math.max(0, seconds);
        int m = s / 60;
        int sec = s % 60;
        return String.format("%02d:%02d", m, sec);
    }
}