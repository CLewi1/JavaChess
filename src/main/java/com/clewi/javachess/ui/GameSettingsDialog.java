package com.clewi.javachess.ui;

import javax.swing.*;
import java.awt.*;

public class GameSettingsDialog extends JDialog {
    private boolean timerEnabled = true;
    private int minutesPerPlayer = 5;
    private int incrementSeconds = 2;
    private boolean confirmed = false;
    
    private JCheckBox enableTimerCheckbox;
    private JSpinner minutesSpinner;
    private JSpinner incrementSpinner;
    
    public GameSettingsDialog(JFrame parent) {
        super(parent, "Game Settings", true);
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setResizable(false);
        
        // Create main panel with dark theme
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(48, 46, 43));
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel titleLabel = new JLabel("Configure Game Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Timer enabled checkbox
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        enableTimerCheckbox = new JCheckBox("Enable Timer");
        enableTimerCheckbox.setSelected(timerEnabled);
        enableTimerCheckbox.setBackground(new Color(48, 46, 43));
        enableTimerCheckbox.setForeground(Color.WHITE);
        enableTimerCheckbox.setFont(new Font("Arial", Font.PLAIN, 14));
        enableTimerCheckbox.addActionListener(e -> updateTimerControls());
        mainPanel.add(enableTimerCheckbox, gbc);
        
        // Minutes per player
        gbc.gridy = 2;
        JLabel minutesLabel = new JLabel("Minutes per player:");
        minutesLabel.setForeground(Color.WHITE);
        minutesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(minutesLabel, gbc);
        
        gbc.gridx = 1;
        minutesSpinner = new JSpinner(new SpinnerNumberModel(minutesPerPlayer, 1, 60, 1));
        minutesSpinner.setPreferredSize(new Dimension(80, 25));
        mainPanel.add(minutesSpinner, gbc);
        
        // Increment seconds
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel incrementLabel = new JLabel("Increment (seconds):");
        incrementLabel.setForeground(Color.WHITE);
        incrementLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(incrementLabel, gbc);
        
        gbc.gridx = 1;
        incrementSpinner = new JSpinner(new SpinnerNumberModel(incrementSeconds, 0, 30, 1));
        incrementSpinner.setPreferredSize(new Dimension(80, 25));
        mainPanel.add(incrementSpinner, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(48, 46, 43));
        
        JButton startButton = createStyledButton("Start");
        startButton.addActionListener(e -> {
            confirmed = true;
            timerEnabled = enableTimerCheckbox.isSelected();
            if (timerEnabled) {
                minutesPerPlayer = (Integer) minutesSpinner.getValue();
                incrementSeconds = (Integer) incrementSpinner.getValue();
            }
            dispose();
        });
        
        JButton cancelButton = createStyledButton("Cancel");
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        updateTimerControls();
        pack();
        setLocationRelativeTo(getParent());
    }
    
    private void updateTimerControls() {
        boolean enabled = enableTimerCheckbox.isSelected();
        minutesSpinner.setEnabled(enabled);
        incrementSpinner.setEnabled(enabled);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
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
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Button border
                g2d.setColor(new Color(30, 30, 30));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(100, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public boolean isTimerEnabled() {
        return timerEnabled;
    }
    
    public int getMinutesPerPlayer() {
        return minutesPerPlayer;
    }
    
    public int getIncrementSeconds() {
        return incrementSeconds;
    }
    
    public int getSecondsPerPlayer() {
        return minutesPerPlayer * 60;
    }
}