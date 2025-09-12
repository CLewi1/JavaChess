package com.clewi.javachess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeScreen extends JFrame {
    private ActionListener gameStartListener;
    
    public HomeScreen() {
        initializeComponents();
    }
    
    public void setGameStartListener(ActionListener listener) {
        this.gameStartListener = listener;
    }
    
    private void initializeComponents() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(45, 45, 65),
                    0, getHeight(), new Color(25, 25, 45)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        JLabel titleLabel = new JLabel("JavaChess");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 72));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 50, 100, 50);
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("A Classic Game of Strategy");
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 24));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 50, 50, 50);
        mainPanel.add(subtitleLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());
        
        // Player vs Player button
        JButton pvpButton = createStyledButton("Player vs Player");
        pvpButton.addActionListener(e -> {
            if (gameStartListener != null) {
                gameStartListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "PVP"));
            }
        });
        
        // Player vs AI button
        JButton pvaiButton = createStyledButton("Player vs AI");
        pvaiButton.addActionListener(e -> {
            if (gameStartListener != null) {
                gameStartListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "PVAI"));
            }
        });
        
        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.gridx = 0;
        buttonGbc.gridy = 0;
        buttonGbc.insets = new Insets(10, 0, 10, 0);
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(pvpButton, buttonGbc);
        
        buttonGbc.gridy = 1;
        buttonPanel.add(pvaiButton, buttonGbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 50, 100, 50);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Button gradient
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(
                        0, 0, new Color(70, 130, 180),
                        0, getHeight(), new Color(50, 100, 150)
                    );
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(
                        0, 0, new Color(90, 150, 200),
                        0, getHeight(), new Color(70, 130, 180)
                    );
                } else {
                    gradient = new GradientPaint(
                        0, 0, new Color(80, 140, 190),
                        0, getHeight(), new Color(60, 120, 170)
                    );
                }
                
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Button border
                g2d.setColor(new Color(40, 80, 120));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(250, 60));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    public void showScreen() {
        setVisible(true);
    }
    
    public void hideScreen() {
        setVisible(false);
    }
}