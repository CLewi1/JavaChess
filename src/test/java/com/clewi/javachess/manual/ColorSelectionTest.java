package com.clewi.javachess.manual;

import com.clewi.javachess.ui.GameSettingsDialog;
import javax.swing.*;

/**
 * Manual test for the new color selection feature in PVAI mode
 * Run this to visually test the GameSettingsDialog with color selection
 */
public class ColorSelectionTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame testFrame = new JFrame("Test Frame");
            testFrame.setSize(400, 300);
            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            testFrame.setLocationRelativeTo(null);
            testFrame.setVisible(true);
            
            // Test PVAI mode dialog (should show color selection)
            GameSettingsDialog pvaiDialog = new GameSettingsDialog(testFrame, "PVAI");
            pvaiDialog.setVisible(true);
            
            if (pvaiDialog.isConfirmed()) {
                System.out.println("PVAI Dialog Results:");
                System.out.println("Timer enabled: " + pvaiDialog.isTimerEnabled());
                System.out.println("Player plays white: " + pvaiDialog.doesPlayerPlayWhite());
                System.out.println("AI plays black: " + !pvaiDialog.doesPlayerPlayWhite());
            }
            
            // Test PVP mode dialog (should NOT show color selection)
            GameSettingsDialog pvpDialog = new GameSettingsDialog(testFrame, "PVP");
            pvpDialog.setVisible(true);
            
            if (pvpDialog.isConfirmed()) {
                System.out.println("\nPVP Dialog Results:");
                System.out.println("Timer enabled: " + pvpDialog.isTimerEnabled());
                // Color selection should not be relevant for PVP
            }
            
            System.exit(0);
        });
    }
}