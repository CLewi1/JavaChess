package com.clewi.javachess;

import com.clewi.javachess.ui.MainApplication;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ChessGame {
    public static void main(String[] args) {
        try {

            System.setProperty("apple.awt.application.appearance", "NSAppearanceNameDarkAqua");

            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e);
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Starting Chess Game...");
                new MainApplication();
            } catch (Exception e) {
                System.err.println("Error starting game: " + e);
                e.printStackTrace();
            }
        });
    }
}
