package com.clewi.javachess.ui;

import com.clewi.javachess.model.GameState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.List;

public class StatusPanel extends JPanel {
    private JLabel statusLabel;
    private JLabel turnLabel;
    private DefaultTableModel tableModel;
    private JTable moveTable;

    public StatusPanel(GameController gameController) {
        setPreferredSize(new Dimension(200, 400));
        setLayout(new BorderLayout());
        
        setBackground(new Color(38,36,33));
        setOpaque(true);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Chess Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE); // Make text visible on dark background
        titlePanel.add(titleLabel);
        
        // Status info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(5, 1, 5, 5));
        infoPanel.setOpaque(false);
        
        statusLabel = new JLabel("Game in progress");
        statusLabel.setForeground(Color.WHITE); // Make text visible on dark background
        turnLabel = new JLabel("White's turn");
        turnLabel.setForeground(Color.WHITE); // Make text visible on dark background

        infoPanel.add(statusLabel);
        infoPanel.add(turnLabel);

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
        buttonPanel.setOpaque(false);
        
        JButton undoButton = new JButton("Undo Move");

        undoButton.addActionListener(e -> gameController.undoMove());
        
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

}