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
    private JTextArea messageArea;

    public StatusPanel(GameController gameController) {
        setPreferredSize(new Dimension(200, 400));
        setLayout(new BorderLayout());
        
        setBackground(new Color(38,36,33));
        setOpaque(true);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Add 20px bottom margin
        titleLabel = new JLabel("Chess Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Move table panel (will be in center)
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        
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
        
        // Increase row height for better spacing
        moveTable.setRowHeight(30); // Default is usually around 16-18, this gives more space

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
        
        // Set preferred size for scroll pane to make table smaller
        scroll.setPreferredSize(new Dimension(200, 150)); // Smaller height for move table
        
        // Add scroll pane to table panel
        tablePanel.add(scroll, BorderLayout.CENTER);
        
        // Create chat box area for game messages (like chess.com)
        JPanel chatBoxContainer = new JPanel(new BorderLayout());
        chatBoxContainer.setBackground(new Color(50, 50, 50));
        chatBoxContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        chatBoxContainer.setOpaque(true);
        chatBoxContainer.setPreferredSize(new Dimension(200, 150));
        
        // Create message area with scrollable text area
        JTextArea messageArea = new JTextArea();
        messageArea.setBackground(new Color(50, 50, 50));
        messageArea.setForeground(Color.WHITE);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 12));
        messageArea.setEditable(false);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(null);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        messageScrollPane.getViewport().setBackground(new Color(50, 50, 50));
        messageScrollPane.setBackground(new Color(50, 50, 50));
        
        // Style the message scroll bar
        JScrollBar messageScrollBar = messageScrollPane.getVerticalScrollBar();
        messageScrollBar.setBackground(new Color(30, 30, 30));
        messageScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 60, 60);
                this.trackColor = new Color(30, 30, 30);
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
        
        chatBoxContainer.add(messageScrollPane, BorderLayout.CENTER);
        
        // Store reference to message area for adding messages
        this.messageArea = messageArea;
        
        // Add initial welcome message
        addMessage("Game ready. Make your move!", Color.WHITE);
        
        // Create a wrapper panel for spacing between table and chat box
        JPanel chatBoxWrapper = new JPanel(new BorderLayout());
        chatBoxWrapper.setOpaque(false);
        chatBoxWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // 10px top margin
        chatBoxWrapper.add(chatBoxContainer, BorderLayout.CENTER);
        
        // Add chat box below the table with spacing
        tablePanel.add(chatBoxWrapper, BorderLayout.SOUTH);

        // Button panel - make it minimal to stay at very bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        buttonPanel.setOpaque(false);
        
        JButton undoButton = new JButton("Undo Move") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Button gradient with gray colors
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(
                        0, 0, new Color(50, 50, 50),
                        0, getHeight(), new Color(30, 30, 30)
                    );
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(
                        0, 0, new Color(80, 80, 80),
                        0, getHeight(), new Color(60, 60, 60)
                    );
                } else {
                    gradient = new GradientPaint(
                        0, 0, new Color(70, 70, 70),
                        0, getHeight(), new Color(50, 50, 50)
                    );
                }
                
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Button border
                g2d.setColor(new Color(30, 30, 30));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                super.paintComponent(g);
            }
        };
        
        undoButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        undoButton.setForeground(Color.WHITE);
        undoButton.setPreferredSize(new Dimension(140, 50));
        undoButton.setFocusPainted(false);
        undoButton.setBorderPainted(false);
        undoButton.setContentAreaFilled(false);
        undoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        undoButton.addActionListener(e -> gameController.undoMove());
        
        buttonPanel.add(undoButton);

        // Add all panels to the status panel
        add(titlePanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Add a message to the chat box area
     */
    public void addMessage(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            if (messageArea.getText().length() > 0) {
                messageArea.append("\n");
            }
            
            // Get current text and add colored message
            String currentText = messageArea.getText();
            messageArea.setText(currentText);
            
            // Create a styled document approach isn't easily supported in JTextArea,
            // so we'll use a simple approach with prefixes for different message types
            String prefix = "";
            if (color == Color.RED) {
                prefix = "⚠ ";
            } else if (color == Color.GREEN) {
                prefix = "✓ ";
            } else if (color == Color.YELLOW || color == Color.ORANGE) {
                prefix = "! ";
            }
            
            messageArea.append(prefix + message);
            
            // Auto-scroll to bottom
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
    }
    
    /**
     * Add an error message in red
     */
    public void addErrorMessage(String message) {
        addMessage(message, Color.RED);
    }
    
    /**
     * Add a success message in green
     */
    public void addSuccessMessage(String message) {
        addMessage(message, Color.GREEN);
    }
    
    /**
     * Add a warning message in yellow
     */
    public void addWarningMessage(String message) {
        addMessage(message, Color.YELLOW);
    }
    
    /**
     * Clear all messages from the chat box
     */
    public void clearMessages() {
        SwingUtilities.invokeLater(() -> {
            messageArea.setText("");
        });
    }
    
    /**
     * Add a game start message
     */
    public void addGameStartMessage() {
        clearMessages();
        addSuccessMessage("New game started!");
    }
    
    /**
     * Add an invalid move message (like chess.com warnings)
     */
    public void addInvalidMoveMessage(String reason) {
        addErrorMessage("Invalid move: " + reason);
    }
    
    /**
     * Add a turn violation message
     */
    public void addTurnViolationMessage(boolean isWhiteTurn) {
        String currentPlayer = isWhiteTurn ? "White" : "Black";
        addErrorMessage("It is " + currentPlayer + "'s turn");
    }
    
    /**
     * Add a check warning message
     */
    public void addCheckWarning() {
        addErrorMessage("Cannot move, king in check");
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
                // Don't add message for normal playing state
                break;
            case CHECK:
                addWarningMessage("Check!");
                break;
            case CHECKMATE:
                addErrorMessage("Checkmate! Game over.");
                break;
            case STALEMATE:
                addMessage("Stalemate - Game is a draw.", Color.CYAN);
                break;
            case DRAW:
                addMessage("Game ended in a draw.", Color.CYAN);
                break;
            case TIMEOUT:
                addErrorMessage("Time's up! Game over.");
                break;
            default:
                addMessage("Unknown game state.", Color.WHITE);
        }
    }
    
    public void setTurn(boolean isWhite) {
        // Don't add a message for normal turn changes
        // Messages will only appear for violations via addTurnViolationMessage()
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