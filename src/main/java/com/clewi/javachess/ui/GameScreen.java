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
    private String gameMode = "PVP";
    private Timer swingClockTimer;
    private ActionListener backToHomeListener;
    private boolean timersEnabled = true;
    
    private JLabel whiteTimerLabel;
    private JLabel blackTimerLabel;
    private JLabel whitePlayerLabel;
    private JLabel blackPlayerLabel;
    
    public GameScreen() {
        setMinimumSize(new Dimension(900, 700));
        gameManager = new GameManager();
        gameManager.initClocks(300, 2);
        DebugUtils.logImportant("Clocks initialized to 5 minutes with 2 second increment.");
        
        // For game state changes
        gameManager.registerObserver(this);
        
        initializeComponents();

        swingClockTimer = new Timer(1000, e -> {
            if (gameManager.isClockEnabled()) {
                gameManager.tick();
                updateTimerLabels();
                if (gameManager.getGameState() == GameState.TIMEOUT) {
                    swingClockTimer.stop();
                }
            } else {
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
        
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        
        JMenuItem backToHome = new JMenuItem("Back to Home");
        backToHome.addActionListener(e -> {
            if (backToHomeListener != null) {
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

        JMenuItem zoomIn = new JMenuItem("Zoom In");
        zoomIn.addActionListener(e -> {
            boardPanel.revalidate();
            boardPanel.repaint();
        });

        JMenuItem zoomOut = new JMenuItem("Zoom Out");
        zoomOut.addActionListener(e -> {
            boardPanel.revalidate();
            boardPanel.repaint();
        });

        viewMenu.add(zoomIn);
        viewMenu.add(zoomOut);

        
        menuBar.add(gameMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
        
        boardPanel = new BoardPanel(gameManager);
        statusPanel = new StatusPanel(this);
        statusPanel.setGameModeTitle(gameMode);
        
        JPanel boardArea = createBoardArea();
        JPanel gameContainer = createCenteredGameContainer(boardArea);
        
        mainPanel.add(gameContainer, BorderLayout.CENTER);
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

        GameState state = event.getGameState();
        
        // Check if AI should make a move (only if game is still playing)
        if (state == GameState.PLAYING || state == GameState.CHECK) {
            if (gameManager.isAITurn()) {
                // Schedule AI move on background thread to avoid blocking UI
                SwingUtilities.invokeLater(() -> triggerAIMove());
            }
        }
        
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
    
    /**
     * Trigger an AI move using SwingWorker for background processing.
     * This prevents UI freezing and provides user feedback while AI thinks.
     */
    private void triggerAIMove() {
        if (!gameManager.isAITurn()) {
            return; // Safety check
        }
        
        DebugUtils.logImportant("Triggering AI move with background processing...");
        
        // Create SwingWorker for background AI processing
        SwingWorker<Boolean, Void> aiWorker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // This runs in background thread
                // Add 2-second delay to make AI moves feel more natural
                Thread.sleep(2000);
                return gameManager.makeAIMove();
            }
            
            @Override
            protected void done() {
                try {
                    // This runs on EDT after background work completes
                    boolean success = get();
                    
                    // Re-enable user input
                    boardPanel.setEnabled(true);
                    
                    // Clear AI thinking message
                    statusPanel.clearAIThinkingMessage();
                    
                    if (!success) {
                        DebugUtils.logImportant("AI failed to make a move");
                        statusPanel.addErrorMessage("AI failed to make a move");
                    } else {
                        DebugUtils.logImportant("AI move completed successfully");
                    }
                } catch (Exception e) {
                    DebugUtils.logImportant("Error during AI move: " + e.getMessage());
                    statusPanel.addErrorMessage("AI move error: " + e.getMessage());
                    boardPanel.setEnabled(true);
                    statusPanel.clearAIThinkingMessage();
                }
            }
        };
        
        // Disable user input while AI thinks
        boardPanel.setEnabled(false);
        
        // Show AI thinking message
        statusPanel.showAIThinkingMessage();
        
        // Start the background AI work
        aiWorker.execute();
    }
    
    public void startNewGame() {
        GameSettingsDialog settingsDialog = new GameSettingsDialog(this, gameMode);
        settingsDialog.setVisible(true);
        
        // If user canceled, don't start a new game
        if (!settingsDialog.isConfirmed()) {
            return;
        }
        
        timersEnabled = settingsDialog.isTimerEnabled();
        gameManager.resetGame();
        
        // Configure AI based on game mode and color selection
        if ("PVAI".equals(gameMode)) {
            boolean playerPlaysWhite = settingsDialog.doesPlayerPlayWhite();
            boolean aiPlaysBlack = playerPlaysWhite; // AI plays opposite color
            gameManager.configureAI(true, aiPlaysBlack);
            
            if (playerPlaysWhite) {
                DebugUtils.logImportant("AI enabled as black player");
            } else {
                DebugUtils.logImportant("AI enabled as white player");
            }
        } else {
            gameManager.configureAI(false, false); // No AI for PVP mode
        }
        
        // Configure clock based on user settings
        if (settingsDialog.isTimerEnabled()) {
            gameManager.initClocks(settingsDialog.getSecondsPerPlayer(), settingsDialog.getIncrementSeconds());
        } else {
            gameManager.disableClocks();
        }
        
        refreshGameLayout();
        
        boardPanel.refresh();
        statusPanel.updateStatus(GameState.PLAYING);
        statusPanel.setTurn(true);

        // Reset move history display
        Board.setMoveHistory(new ArrayList<>());
        statusPanel.updateMoveHistory(gameManager.getDisplayMoveHistory());
        
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
        
        statusPanel.addGameStartMessage();
    }
    
    private void refreshGameLayout() {
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
        
        JPanel boardArea = createBoardArea();
        JPanel gameContainer = createCenteredGameContainer(boardArea);
        mainPanel.add(gameContainer, BorderLayout.CENTER);
        
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
            
            if (!fileToSave.getName().toLowerCase().endsWith(".chess")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".chess");
            }
            
            if (fileToSave.exists()) {
                int response = JOptionPane.showConfirmDialog(this,
                        "The file already exists. Do you want to overwrite it?", 
                        "Confirm Overwrite", JOptionPane.YES_NO_OPTION);
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
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
            
            boolean success = gameManager.loadGame(fileToLoad);
            if (success) {
                boardPanel.refresh();
                statusPanel.updateStatus(gameManager.getGameState());
                statusPanel.setTurn(gameManager.getCurrentPlayer().isWhite());
                statusPanel.updateMoveHistory(gameManager.getDisplayMoveHistory());
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
        // In PVAI mode, we want to undo both the AI's move and the player's move
        // so the player gets back to their turn before their last move
        if ("PVAI".equals(gameMode) && gameManager.isAIEnabled()) {
            // First, check if we have at least 2 moves to undo
            if (Board.getMoveHistory().size() < 2) {
                JOptionPane.showMessageDialog(this, 
                        "Need at least 2 moves to undo in AI mode", 
                        "Undo Move", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Undo the AI's move first
            boolean firstUndo = gameManager.undoMove();
            if (firstUndo) {
                // Then undo the player's move
                boolean secondUndo = gameManager.undoMove();
                if (secondUndo) {
                    // Successful double undo - refresh UI
                    boardPanel.refresh();
                    statusPanel.updateStatus(gameManager.getGameState());
                    statusPanel.setTurn(gameManager.getCurrentPlayer().isWhite());
                    statusPanel.updateMoveHistory(gameManager.getDisplayMoveHistory());
                    DebugUtils.logImportant("Successfully undid both AI and player moves in PVAI mode");
                } else {
                    // Second undo failed - this shouldn't happen if we checked properly
                    JOptionPane.showMessageDialog(this, 
                            "Failed to undo player move", 
                            "Undo Move", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // First undo failed
                JOptionPane.showMessageDialog(this, 
                        "Failed to undo AI move", 
                        "Undo Move", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // PVP mode or AI disabled - undo single move as before
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
    }
    
    public void showScreen() {
        setVisible(true);
    }
    
    public void hideScreen() {
        setVisible(false);
    }
    
    private JPanel createBoardArea() {
        createPlayerNameLabels();
        
        if (timersEnabled) {
            createTimerPanels();
            return createBoardAreaWithTimers();
        } else {
            return createBoardAreaWithoutTimers();
        }
    }
    
    private void createTimerPanels() {
        whiteTimerLabel = new JLabel("05:00", SwingConstants.CENTER);
        whiteTimerLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        whiteTimerLabel.setForeground(Color.BLACK);
        whiteTimerLabel.setBackground(new Color(255,255,255));
        whiteTimerLabel.setOpaque(true);

        blackTimerLabel = new JLabel("05:00", SwingConstants.CENTER);
        blackTimerLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        blackTimerLabel.setForeground(Color.WHITE);
        blackTimerLabel.setBackground(new Color(38,36,33));
        blackTimerLabel.setOpaque(true);
    }
    
    private void createPlayerNameLabels() {
        // Determine player names based on game mode and AI configuration
        String whitePlayerName, blackPlayerName;
        if ("PVAI".equals(gameMode)) {
            // Check if AI is playing as black (aiAsBlack = true) or white (aiAsBlack = false)
            if (gameManager.isAIAsBlack()) {
                whitePlayerName = "Player";
                blackPlayerName = "AI";
            } else {
                whitePlayerName = "AI";
                blackPlayerName = "Player";
            }
        } else {
            whitePlayerName = "Player 1";
            blackPlayerName = "Player 2";
        }
        
        whitePlayerLabel = new JLabel(whitePlayerName, SwingConstants.LEFT);
        whitePlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        whitePlayerLabel.setForeground(Color.WHITE);
        
        blackPlayerLabel = new JLabel(blackPlayerName, SwingConstants.LEFT);
        blackPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        blackPlayerLabel.setForeground(Color.WHITE);
    }
    
    private JPanel createBoardAreaWithoutTimers() {
        JPanel boardArea = new JPanel();
        boardArea.setLayout(null);
        boardArea.setOpaque(false);
        
        int boardSize = 65 * 8;
        int boardX = 120; 
        int boardY = 60;
        boardPanel.setBounds(boardX, boardY, boardSize, boardSize);
        
        blackPlayerLabel.setBounds(boardX, boardY - 40, 100, 30);
        whitePlayerLabel.setBounds(boardX, boardY + boardSize + 10, 100, 30);
        
        boardArea.setPreferredSize(new Dimension(boardSize + 140, boardSize + 120));
        
        boardArea.add(boardPanel);
        boardArea.add(whitePlayerLabel);
        boardArea.add(blackPlayerLabel);
        
        return boardArea;
    }
    
    private JPanel createBoardAreaWithTimers() {
        JPanel boardArea = new JPanel();
        boardArea.setLayout(null);
        boardArea.setOpaque(false);
        
        int boardSize = 65 * 8;
        int boardX = 120;
        int boardY = 60;
        boardPanel.setBounds(boardX, boardY, boardSize, boardSize);
        
        whiteTimerLabel.setBounds(boardX + boardSize - 100, boardY + boardSize + 10, 100, 30);
        blackTimerLabel.setBounds(boardX + boardSize - 100, boardY - 40, 100, 30);
        
        blackPlayerLabel.setBounds(boardX, boardY - 40, 100, 30);
        whitePlayerLabel.setBounds(boardX, boardY + boardSize + 10, 100, 30);
        
        boardArea.setPreferredSize(new Dimension(boardSize + 140, boardSize + 120));
        
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
        gameContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
        
        int boardHeight = 65 * 8;
        statusPanel.setPreferredSize(new Dimension(200, boardHeight));
        
        gameContainer.add(boardArea);
        gameContainer.add(statusPanel);
        
        return gameContainer;
    }
    
    private void updateTimerLabels() {
        if (!timersEnabled || whiteTimerLabel == null || blackTimerLabel == null) {
            return;
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