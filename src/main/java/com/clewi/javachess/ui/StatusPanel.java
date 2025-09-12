package com.clewi.javachess.ui;

import com.clewi.javachess.model.GameState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.List;

public class StatusPanel extends JPanel {
    private JLabel statusLabel;
    private JLabel turnLabel;
    private GameController gameController;
    private JButton newGameButton;
    private DefaultTableModel tableModel;
    private JTable moveTable;
    private JPanel whiteTimerPanel;
    private JLabel whiteTimerLabel;
    private JPanel blackTimerPanel;
    private JLabel blackTimerLabel;

    public StatusPanel(GameController gameController) {
        this.gameController = gameController;
        setPreferredSize(new Dimension(200, 400));
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Chess Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        
        // Status info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(5, 1, 5, 5));
        
        statusLabel = new JLabel("Game in progress");
        turnLabel = new JLabel("White's turn");

        whiteTimerPanel = new JPanel(new BorderLayout());
        whiteTimerPanel.setBorder(BorderFactory.createTitledBorder("White Timer"));
        whiteTimerLabel = new JLabel("00:00");
        whiteTimerPanel.add(whiteTimerLabel, BorderLayout.CENTER);
        blackTimerPanel = new JPanel(new BorderLayout());
        blackTimerPanel.setBorder(BorderFactory.createTitledBorder("Black Timer"));
        blackTimerLabel = new JLabel("00:00");
        blackTimerPanel.add(blackTimerLabel, BorderLayout.CENTER);
        
        infoPanel.add(statusLabel);
        infoPanel.add(turnLabel);
        infoPanel.add(whiteTimerPanel);
        infoPanel.add(blackTimerPanel);

        // Move history table (two columns: White | Black)
        tableModel = new DefaultTableModel(new Object[] { "White", "Black" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        moveTable = new JTable(tableModel);
        moveTable.setFillsViewportHeight(true);
        moveTable.setRowSelectionAllowed(false);

        JScrollPane scroll = new JScrollPane(moveTable);
        add(scroll, BorderLayout.CENTER);
        infoPanel.add(scroll);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 5, 5));
        
        newGameButton = new JButton("New Game");
        JButton saveButton = new JButton("Save Game");
        JButton loadButton = new JButton("Load Game");
        JButton undoButton = new JButton("Undo Move");

        newGameButton.addActionListener(e -> gameController.startNewGame());
        saveButton.addActionListener(e -> gameController.saveGame());
        loadButton.addActionListener(e -> gameController.loadGame());
        undoButton.addActionListener(e -> gameController.undoMove());
        
        buttonPanel.add(newGameButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(undoButton);

        // Add all panels to the status panel
        add(titlePanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    public void updateStatus(GameState state) {
        switch (state) {
            case PLAYING:
                statusLabel.setText("Game in progress");
                statusLabel.setForeground(Color.BLACK);
                break;
            case CHECK:
                statusLabel.setText("Check!");
                statusLabel.setForeground(Color.RED);
                break;
            case CHECKMATE:
                statusLabel.setText("Checkmate!");
                statusLabel.setForeground(Color.RED);
                break;
            case STALEMATE:
                statusLabel.setText("Stalemate");
                statusLabel.setForeground(Color.BLUE);
                break;
            case DRAW:
                statusLabel.setText("Draw");
                statusLabel.setForeground(Color.BLUE);
                break;
            case TIMEOUT:
                statusLabel.setText("Timeout");
                statusLabel.setForeground(Color.RED);
                break;
            default:
                statusLabel.setText("Unknown state");
                statusLabel.setForeground(Color.BLACK);
        }
    }
    
    public void setTurn(boolean isWhite) {
        turnLabel.setText((isWhite ? "White" : "Black") + "'s turn");
    }

     public void updateMoveHistory(List<?> moves) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            if (moves == null || moves.isEmpty()) {
                return;
            }

            int rows = (moves.size() + 1) / 2;
            for (int r = 0; r < rows; r++) {
                int whiteIndex = r * 2;
                int blackIndex = whiteIndex + 1;
                String whiteText = whiteIndex < moves.size() && moves.get(whiteIndex) != null ? moves.get(whiteIndex).toString() : "";
                String blackText = blackIndex < moves.size() && moves.get(blackIndex) != null ? moves.get(blackIndex).toString() : "";
                tableModel.addRow(new Object[] { whiteText, blackText });
            }

            // Keep scroll at bottom to show latest moves
            if (moveTable.getRowCount() > 0) {
                int lastRow = moveTable.getRowCount() - 1;
                moveTable.scrollRectToVisible(moveTable.getCellRect(lastRow, 0, true));
            }
        });
    }

    public void updateTimers(int whiteSeconds, int blackSeconds) {
        SwingUtilities.invokeLater(() -> {
            // If you have GameManager.formatSecondsAsClock(...) you can use it.
            // Otherwise use simple mm:ss formatting:
            String whiteText = formatSecondsAsClock(whiteSeconds);
            String blackText = formatSecondsAsClock(blackSeconds);
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