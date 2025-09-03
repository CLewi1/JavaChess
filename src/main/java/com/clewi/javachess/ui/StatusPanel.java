package com.clewi.javachess.ui;

import com.clewi.javachess.model.GameState;
import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private JLabel statusLabel;
    private JLabel turnLabel;
    private ChessGUI parentGUI;
    private JButton newGameButton;
    
    public StatusPanel(ChessGUI parent) {
        this.parentGUI = parent;
        setPreferredSize(new Dimension(200, 400));
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Chess Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        
        // Status info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(4, 1, 5, 10));
        
        statusLabel = new JLabel("Game in progress");
        turnLabel = new JLabel("White's turn");
        
        infoPanel.add(statusLabel);
        infoPanel.add(turnLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 5, 5));
        
        newGameButton = new JButton("New Game");
        JButton saveButton = new JButton("Save Game");
        JButton loadButton = new JButton("Load Game");
        
        newGameButton.addActionListener(e -> parentGUI.startNewGame());
        saveButton.addActionListener(e -> parentGUI.saveGame());
        loadButton.addActionListener(e -> parentGUI.loadGame());
        
        buttonPanel.add(newGameButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        
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
        }
    }
    
    public void setTurn(boolean isWhite) {
        turnLabel.setText((isWhite ? "White" : "Black") + "'s turn");
    }
}
