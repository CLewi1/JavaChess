package com.clewi.javachess.ui;

import com.clewi.javachess.model.GameState;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.List;

public class StatusPanel extends JPanel {
    private JLabel statusLabel;
    private JLabel turnLabel;
    private JLabel titleLabel;
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
        titleLabel = new JLabel("Chess Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Status info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(3, 1, 5, 5));
        infoPanel.setOpaque(false);
        
        statusLabel = new JLabel("Game in progress");
        statusLabel.setForeground(Color.WHITE);
        turnLabel = new JLabel("White's turn");
        turnLabel.setForeground(Color.WHITE);

        // Move history table (three columns: # | White | Black)
        tableModel = new DefaultTableModel(new Object[] { "#", "White", "Black" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        moveTable = new JTable(tableModel);
        moveTable.setFillsViewportHeight(true);
        moveTable.setRowSelectionAllowed(false);
        moveTable.setColumnSelectionAllowed(false);
        moveTable.setCellSelectionEnabled(false);
        moveTable.setFocusable(false);

        // Set dark theme colors for the table
        moveTable.setBackground(new Color(40, 40, 40));
        moveTable.setForeground(Color.WHITE);
        moveTable.setGridColor(new Color(60, 60, 60));
        moveTable.setSelectionBackground(new Color(70, 70, 70));
        moveTable.setSelectionForeground(Color.WHITE);
        
        // Custom renderer for alternating row colors
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        comp.setBackground(new Color(40, 40, 40)); // Dark gray for even rows
                    } else {
                        comp.setBackground(new Color(50, 50, 50)); // Slightly lighter for odd rows
                    }
                }
                comp.setForeground(Color.WHITE);
                setHorizontalAlignment(SwingConstants.CENTER);
                return comp;
            }
        };
        
        // Apply the renderer to all three columns
        moveTable.getColumnModel().getColumn(0).setCellRenderer(renderer); // # column
        moveTable.getColumnModel().getColumn(1).setCellRenderer(renderer); // White column
        moveTable.getColumnModel().getColumn(2).setCellRenderer(renderer); // Black column
        
        // Style the table header
        moveTable.getTableHeader().setBackground(new Color(30, 30, 30));
        moveTable.getTableHeader().setForeground(Color.WHITE);
        moveTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Center the header text
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setBackground(new Color(30, 30, 30));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Arial", Font.BOLD, 12));
        moveTable.getTableHeader().setDefaultRenderer(headerRenderer);
        
        // Disable column reordering
        moveTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths (narrower for move numbers)
        moveTable.getColumnModel().getColumn(0).setPreferredWidth(30); // # column
        moveTable.getColumnModel().getColumn(1).setPreferredWidth(85); // White column
        moveTable.getColumnModel().getColumn(2).setPreferredWidth(85); // Black column
        moveTable.getColumnModel().getColumn(0).setMaxWidth(40);
        moveTable.getColumnModel().getColumn(0).setMinWidth(25);
        
        // Remove table border completely
        moveTable.setBorder(null);
        moveTable.setShowGrid(false);
        moveTable.setIntercellSpacing(new Dimension(0, 0)); // Remove spacing between cells

        JScrollPane scroll = new JScrollPane(moveTable);
        scroll.setBorder(null); // Remove the scroll pane border
        scroll.setViewportBorder(null); // Remove viewport border
        scroll.getViewport().setBackground(new Color(40, 40, 40)); // Match table background
        scroll.setBackground(new Color(40, 40, 40));
        
        // Make scroll bar always visible
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Style the scroll bar for dark mode
        JScrollBar verticalScrollBar = scroll.getVerticalScrollBar();
        verticalScrollBar.setBackground(new Color(30, 30, 30));
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 60, 60);
                this.trackColor = new Color(30, 30, 30);
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                    return;
                }
                
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                
                // Create rounded rectangle with padding for better appearance
                int arc = 8; // Roundness of corners
                int padding = 2;
                g2.fillRoundRect(thumbBounds.x + padding, thumbBounds.y + padding, 
                               thumbBounds.width - 2 * padding, thumbBounds.height - 2 * padding, arc, arc);
                g2.dispose();
            }
            
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        add(scroll, BorderLayout.CENTER);
        infoPanel.add(scroll);
        infoPanel.add(statusLabel);
        infoPanel.add(turnLabel);

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
    }

    /**
     * Update the title to reflect the selected game mode.
     * Accepted values for mode are: "PVP" (player vs player) and "PVAI" (player vs AI).
     * Any other value will default to "Player vs. Player".
     */
    public void setGameModeTitle(String mode) {
        if (mode != null && mode.equals("PVAI")) {
            titleLabel.setText("Player vs. AI");
        } else {
            titleLabel.setText("Player vs. Player");
        }
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
                String moveNumber = String.valueOf(r + 1) + "."; // Move numbers start from 1
                String whiteText = whiteIndex < moves.size() && moves.get(whiteIndex) != null ? moves.get(whiteIndex).toString() : "";
                String blackText = blackIndex < moves.size() && moves.get(blackIndex) != null ? moves.get(blackIndex).toString() : "";
                tableModel.addRow(new Object[] { moveNumber, whiteText, blackText });
            }

            // Keep scroll at bottom to show latest moves
            if (moveTable.getRowCount() > 0) {
                int lastRow = moveTable.getRowCount() - 1;
                moveTable.scrollRectToVisible(moveTable.getCellRect(lastRow, 0, true));
            }
        });
    }

}